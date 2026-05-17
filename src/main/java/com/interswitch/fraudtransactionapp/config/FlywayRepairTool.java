package com.interswitch.fraudtransactionapp.config;


import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Value;

public class FlywayRepairTool {

    public static void main(String[] args) {


        Flyway flyway = Flyway.configure()

                .dataSource(
                        "jdbc:sqlserver://localhost:1433;databaseName=fraud_db;encrypt=true;trustServerCertificate=true",
                        "localhost",
                        "agbajelola123!")
                .load();

        flyway.repair();

        System.out.println("Flyway repair completed successfully!");
    }
}