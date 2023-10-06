package com.example.labs.DataBase;


import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DataBaseHandler extends Configs {
    Connection dbConnection;
    public Connection getDbConnection() {
        String connectionString = "jdbc:mysql://" + dbHost + ":"+
                dbPort + "/" + dbName;
        try {
            dbConnection = DriverManager.getConnection(connectionString,dbUser,dbPass);
        } catch (SQLException ex) {
            Logger.getLogger(DataBaseHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return dbConnection;
    }

    public HashMap<Integer,String> getEnterprisesNameAndID(){
        HashMap<Integer,String> hashMap = new HashMap<>();

        try {
            // Підключення до бази даних
            Connection connection = getDbConnection();

            // Запит до бази даних для вибору всіх імен зі стовпця "name"
            String sql = "SELECT " + Const.OBJECT_ID + ", " + Const.OBJECT_NAME + " FROM " + Const.OBJECT_TABLE;

            Statement statement = connection.createStatement();
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
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return hashMap;
    }

    public HashMap<Integer,String> getPollutantsCodeAndName(){
        HashMap<Integer,String> pollutantsList = new HashMap<>();

        try {
            // Підключення до бази даних
            Connection connection = getDbConnection();

            // Запит до бази даних для вибору всіх імен зі стовпця "name"
            String sql = "SELECT " + Const.POLLUTANT_CODE + ", " + Const.POLLUTANT_NAME + " FROM " + Const.POLLUTANT_TABLE;

            Statement statement = connection.createStatement();
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
            connection.close();
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

        ResultSet resSet;
        String select = "SELECT " + column + " FROM " + table + " WHERE " +
                tableId + "=?";
        try (PreparedStatement preparedStatement = getDbConnection().prepareStatement(select)) {
            preparedStatement.setInt(1, id);
            resSet = preparedStatement.executeQuery();

            if (resSet.next()) {
                // Отримуємо значення з вибраного стовпця
                return resSet.getString(column);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null; // Повертаємо null у разі відсутності результату
    }

    public int getMaxIdFromTable(String table) {
        String idColumnName = (table.equals(Const.POLLUTANT_TABLE) || table.equals(Const.POLLUTION_TABLE)) ?
                (table.equals(Const.POLLUTANT_TABLE) ? Const.POLLUTANT_CODE : Const.POLLUTION_ID) : Const.OBJECT_ID;
        String getMaxIdQuery = "SELECT MAX(" + idColumnName + ") FROM " + table;
        Statement statement;
        int maxId = 1;
        try {
            statement = getDbConnection().createStatement();
            ResultSet resultSet = statement.executeQuery(getMaxIdQuery);
            if (resultSet.next()) {
                maxId = resultSet.getInt(1);
            }
            statement.close();
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


}
