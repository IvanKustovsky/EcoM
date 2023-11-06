package com.example.labs.DataBase;

import com.example.labs.Services.Calculations;
import com.example.labs.Controllers.AddEnterpriseController;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DataBaseHandler extends Configs {
    private static DataBaseHandler instance = null;
    private Connection dbConnection;
    private PreparedStatement preparedStatement;

    private DataBaseHandler() {
        // Приватний конструктор
    }

    public static DataBaseHandler getInstance() {
        if (instance == null) {
            instance = new DataBaseHandler();
        }
        return instance;
    }

    public Connection getDbConnection() {
        if (dbConnection != null) {
            return dbConnection;
        }
        String connectionString = "jdbc:mysql://" + dbHost + ":" + dbPort + "/" + dbName;
        try {
            dbConnection = DriverManager.getConnection(connectionString, dbUser, dbPass);
        } catch (SQLException ex) {
            Logger.getLogger(DataBaseHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return dbConnection;
    }

    public HashMap<Integer,String> getEnterprisesNameAndID(){
        HashMap<Integer,String> hashMap = new HashMap<>();

        try {
            // Підключення до бази даних
             dbConnection = getDbConnection();

            // Запит до бази даних для вибору всіх імен зі стовпця "name"
            String sql = "SELECT " + Const.OBJECT_ID + ", " + Const.OBJECT_NAME + " FROM " + Const.OBJECT_TABLE;

            Statement statement = dbConnection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            // Додавання імен до списку
            while (resultSet.next()) {
                int id = resultSet.getInt(Const.OBJECT_ID);
                String name = resultSet.getString(Const.OBJECT_NAME);
                hashMap.put(id,name);
            }

            // Закриття ресурсів
            resultSet.close();
            statement.close();
            //dbConnection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return hashMap;
    }

    public HashMap<Integer,String> getPollutantsCodeAndName(){
        HashMap<Integer,String> pollutantsList = new HashMap<>();

        try {
            // Підключення до бази даних
            dbConnection = getDbConnection();

            // Запит до бази даних для вибору всіх імен зі стовпця "name"
            String sql = "SELECT " + Const.POLLUTANT_CODE + ", " + Const.POLLUTANT_NAME + " FROM " + Const.POLLUTANT_TABLE;

            Statement statement = dbConnection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            // Додавання імен до списку
            while (resultSet.next()) {
                int code = resultSet.getInt(Const.POLLUTANT_CODE);
                String name = resultSet.getString(Const.POLLUTANT_NAME);
                pollutantsList.put(code,name);
            }

            // Закриття ресурсів
            resultSet.close();
            statement.close();
            //dbConnection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pollutantsList;
    }

    public int getKeyByValue(HashMap<Integer,String> map, String value){
        int key = 0;
        for (Map.Entry<Integer, String> entry : map.entrySet()) {
            if (entry.getValue().equals(value)) {
                key = entry.getKey();
                break; // Якщо значення знайдено, можна вийти з циклу
            }
        }
        return key;
    }

    public String getTableColumnById(String table, String column, int id) {
        String tableId;
        if (table.equals(Const.POLLUTANT_TABLE) || table.equals(Const.OBJECT_TABLE)) {
            tableId = table.equals(Const.OBJECT_TABLE) ? Const.OBJECT_ID : Const.POLLUTANT_CODE;
        } else {
            tableId = Const.POLLUTION_ID;
        }

        String select = "SELECT " + column + " FROM " + table + " WHERE " +
                tableId + "=?";
        try {
            dbConnection = getDbConnection();
            PreparedStatement preparedStatement = dbConnection.prepareStatement(select);
            preparedStatement.setInt(1, id);
            ResultSet resSet = preparedStatement.executeQuery();

            if (resSet.next()) {
                // Отримуємо значення з вибраного стовпця
                return resSet.getString(column);
            }
            resSet.close();
            preparedStatement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null; // Повертаємо null у разі відсутності результату.
    }

    public int getMaxIdFromTable(String table) {
        String idColumnName = (table.equals(Const.POLLUTANT_TABLE) || table.equals(Const.POLLUTION_TABLE)) ?
                (table.equals(Const.POLLUTANT_TABLE) ? Const.POLLUTANT_CODE : Const.POLLUTION_ID) : Const.OBJECT_ID;
        String getMaxIdQuery = "SELECT MAX(" + idColumnName + ") FROM " + table;
        Statement statement;
        int maxId = 1;
        try {
            dbConnection = getDbConnection();
            statement = dbConnection.createStatement();
            ResultSet resultSet = statement.executeQuery(getMaxIdQuery);
            if (resultSet.next()) {
                maxId = resultSet.getInt(1);
            }
            statement.close();
            resultSet.close();
            //dbConnection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return maxId;
    }

    public boolean isIdContains(int id){
        HashMap<Integer,String> map = getEnterprisesNameAndID();
        return map.containsKey(id);
    }

    public boolean isCodeContains(int code){
        HashMap<Integer,String> map = getPollutantsCodeAndName();
        return map.containsKey(code);
    }

    public int isContainsRecordByEnterprisePollutantAndYear(int id_object, int pollutantCode,int year){
        // Перевірка, чи існує запис з такими ж ім'ям підприємства, назвою забрудника речовини та роком
        String checkQuery = "SELECT " + Const.POLLUTION_ID + " FROM " + Const.POLLUTION_TABLE +
                " WHERE " + Const.POLLUTION_OBJECT_ID + " = ? AND " +
                Const.POLLUTION_CODE_POLLUTANT + " = ? AND " + Const.POLLUTION_YEAR + " = ?";
        dbConnection = getDbConnection();
        try {
            PreparedStatement checkStatement = dbConnection.prepareStatement(checkQuery);
            checkStatement.setInt(1, id_object);
            checkStatement.setInt(2, pollutantCode);
            checkStatement.setInt(3, year);
            ResultSet resultSet = checkStatement.executeQuery();
            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                if (count > 0) {
                    return resultSet.getInt(Const.POLLUTION_ID);
                }
            }
            resultSet.close();
            checkStatement.close();
            dbConnection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return -1;
    }

    public PreparedStatement getInsertPollutionPreparedStatement(String query, int id_object,
            int pollutantCode, double concentration, double pollution, int year) {
        try {
            dbConnection = getDbConnection();
            preparedStatement = dbConnection.prepareStatement(query);
            double hq = 0, cr = 0, rfc, sf, gdk, mass_consumption, compensation = 0;
            String rfcStr = getTableColumnById(Const.POLLUTANT_TABLE, Const.POLLUTANT_RFC, pollutantCode);
            String sfStr = getTableColumnById(Const.POLLUTANT_TABLE, Const.POLLUTANT_SF, pollutantCode);
            String gdkStr = getTableColumnById(Const.POLLUTANT_TABLE, Const.POLLUTANT_GDK, pollutantCode);
            String mass_consumptionStr = getTableColumnById(Const.POLLUTANT_TABLE,
                    Const.POLLUTANT_MASS_CONSUMPTION, pollutantCode);
            try {
                gdk = Double.parseDouble(gdkStr);
                mass_consumption = Double.parseDouble(mass_consumptionStr);
                rfc = Double.parseDouble(rfcStr);
                sf = Double.parseDouble(sfStr);
                hq = Calculations.CalcHq(concentration, rfc);
                cr = Calculations.CalcCR(concentration, sf);
                compensation = Calculations.calcCompensation(pollution, mass_consumption, gdk);
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }

            preparedStatement.setInt(1, id_object);
            preparedStatement.setInt(2, pollutantCode);
            preparedStatement.setDouble(3, pollution);
            preparedStatement.setDouble(4, concentration);
            preparedStatement.setDouble(5, hq);
            preparedStatement.setDouble(6, cr);
            preparedStatement.setDouble(7, compensation);
            preparedStatement.setInt(8, year);

            return preparedStatement; // Додано повернення PreparedStatement
        } catch (SQLException ex) {
            Logger.getLogger(AddEnterpriseController.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null; // У випадку помилки повертається null
    }

    public void executeInsertProcess(String query, int id_object, int pollutantCode,
                                     double concentration, double pollution, int year){
        try {
            preparedStatement = getInsertPollutionPreparedStatement(query,id_object,pollutantCode,
                    concentration,pollution,year);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void executeUpdateProcess(Connection connection, String query, int id_object,
                                     int pollutantCode, double concentration, double pollution, int year,int pollutionId){
        try {
            preparedStatement = getInsertPollutionPreparedStatement(query,id_object,pollutantCode,
                    concentration,pollution,year);
            preparedStatement.setInt(9,pollutionId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
