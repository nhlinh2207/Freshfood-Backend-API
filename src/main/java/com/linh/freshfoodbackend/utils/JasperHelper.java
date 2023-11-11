package com.linh.freshfoodbackend.utils;

import com.linh.freshfoodbackend.exception.UnSuccessException;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;
import net.sf.jasperreports.engine.export.*;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRRtfExporter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.engine.query.JRQueryExecuterFactory;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.export.*;

import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.File;
import java.sql.Connection;
import java.util.Map;
import java.util.Set;

public class JasperHelper {

//    public static void printReport(String exportType, File reportFile, Map<String, Object> parameters, DataSource dataSource, HttpServletResponse response) {
//        try {
//            Connection conn = dataSource.getConnection();
//            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(reportFile);
//            String fileName = reportFile.getName();
//            jasperReport.setProperty(JRParameter.REPORT_QUERY_EXECUTER_FACTORY, "com.jaspersoft.jrx.query.SqlQueryExecuterFactory");
//
//        }catch (Exception e){
//            e.printStackTrace();
//            throw new UnSuccessException(e.getMessage());
//        }
//    }

    // Just for debug
    private static void printInfo(String fileName, Map<String, Object> parameters) {
        // Debug start
        System.out.println("\nReport file name: " + fileName);
        Set<Map.Entry<String, Object>> paramsSet = parameters.entrySet();
        for (Map.Entry<String, Object> i : paramsSet){
            System.out.println("Param [" + i.getKey() + "]: " + i.getValue());
        }
        System.out.println("End " + fileName + "\n");
        // Debug end
    }
}
