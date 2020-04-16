package com.self.util;

import com.self.model.Item;
import javafx.collections.ObservableList;
import org.apache.poi.hssf.record.crypto.Biff8EncryptionKey;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.crypt.Decryptor;
import org.apache.poi.poifs.crypt.EncryptionInfo;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.security.GeneralSecurityException;
import java.util.*;

public class ExcelUtil {

    /**
     * store data from daily report to my report
     * @param path
     * @param items
     * @throws IOException
     */
    public static void persistDataToExcel(String path, ObservableList<Item> items) throws IOException {
        try (Workbook workBook = getWorkBook(path);
             FileOutputStream fileOutputStream = new FileOutputStream(path)) {

            for (Item item : items) {
                if (item.getCompany() != null && !item.getCompany().trim().equals("")) {
                    Sheet sheetAt = workBook.getSheet(item.getCompany());

                    if (sheetAt == null) {
                        sheetAt = createSheet(workBook, item.getCompany());
                    }

                    Row row = sheetAt.createRow(sheetAt.getLastRowNum() + 1);

                    row.createCell(0).setCellValue(item.getInDate(true));
                    row.createCell(1).setCellValue(item.getName());
                    row.createCell(2).setCellValue(item.getSpec());
                    row.createCell(3).setCellValue(item.getToStorage());
                }
            }
            workBook.write(fileOutputStream);
        }
    }

    /**
     * get different companies between report and document file
     * @param path
     * @param items
     * @throws IOException
     */
    public static ArrayList<Item> getDifferenceInCompanies(String path, ObservableList<Item> items) throws IOException {
        try (Workbook workBook = getWorkBook(path)) {
            ArrayList<Item> diff = new ArrayList<>();
            for (Item item : items) {
                if (item.getCompany() != null && !item.getCompany().trim().equals("")) {
                    Sheet sheetAt = workBook.getSheet(item.getCompany());

                    if (sheetAt == null) {
                        diff.add(item);
                    }
                }
            }
            return diff;
        }
    }

    /**
     * if there's no corresponding company name on my report
     * generate a new sheet for the company
     * @param workBook
     * @param company
     * @return
     */
    private static Sheet createSheet(Workbook workBook, String company) {
        Sheet sheet = workBook.createSheet(company);
        Row row = sheet.createRow(0);
        Cell cell = row.createCell(0, CellType.STRING);
        cell.setCellValue(company);

        String[] headers = {"收货日期", "货物名称", "规格", "数量",
                "单价", "开票总额", "票号", "付款日期",
                "摘要", "付款金额", "应付帐款余额", "备注"};

        Row row1 = sheet.createRow(1);
        int index = 0;
        for (String s:
             headers) {
            row1.createCell(index, CellType.STRING).setCellValue(s);
            index++;
        }

        return sheet;
    }

    /**
     * get all company names
     *
     * @param path
     * @return
     * @throws IOException
     * @throws GeneralSecurityException
     */
    public static List<String> getCompanyList(String path) throws IOException, GeneralSecurityException {
        ArrayList<String> items = new ArrayList<String>();

        try (FileInputStream fileInputStream = new FileInputStream(path)) {
            try (Workbook workBook = WorkbookFactory.create(fileInputStream);) {

                Iterator<Sheet> sheetIterator = workBook.sheetIterator();

                while (sheetIterator.hasNext()) {
                    Sheet next = sheetIterator.next();
                    items.add(next.getSheetName().trim());
                }

                return items;
            }
        }
    }

    /**
     * get report content
     * can be used for both xls and xlsx file
     *
     * @param path absolute path of the excel file
     * @return a list contains all content
     */
    public static List<Item> getReportContent(String path, Date start, Date end) throws IOException, GeneralSecurityException {
        ArrayList<Item> items = new ArrayList<Item>();
        try (Workbook workBook = getWorkBook(path)) {

            readSheet(workBook.getSheetAt(0), items, start, end);

            return items;
        }
    }

    /**
     * open xls/xlsx file without password
     *
     * @param path excel file path
     * @return excel work book
     * @throws IOException
     */
    private static Workbook getWorkBook(String path) throws IOException {
        try (FileInputStream fileInputStream = new FileInputStream(path)) {
            return WorkbookFactory.create(fileInputStream);
        }
    }

    /**
     * get work book
     * can be used for both xls and xlsx file
     *
     * @param path absolute path
     * @param pwd  password
     * @return WorkBook object from excel file
     * @throws IOException
     * @throws GeneralSecurityException
     */
    private static Workbook getWorkBook(String path, String pwd) throws IOException, GeneralSecurityException {
        Workbook workBook;
        try (POIFSFileSystem fileSystem = new POIFSFileSystem(new File(path))) {

            if (path.endsWith(".xls")) {
                Biff8EncryptionKey.setCurrentUserPassword(pwd);
                workBook = new HSSFWorkbook(fileSystem);
            } else {
                EncryptionInfo info = new EncryptionInfo(fileSystem);
                Decryptor decryptor = Decryptor.getInstance(info);
                decryptor.verifyPassword(pwd);
                try (InputStream dataStream = decryptor.getDataStream(fileSystem)) {
                    workBook = new SXSSFWorkbook(new XSSFWorkbook(dataStream));
                }
            }

            return workBook;
        }
    }

    /**
     * read one sheet, get item name, spec and inStorage
     * can be used for both xls and xlsx
     * I assume that the spec column is next to name column
     * I assume that the item list starts from row index 4
     * add items to the given list
     *
     * @param sheet sheet you want read
     */
    private static void readSheet(Sheet sheet, List<Item> items, Date start, Date end) {
        int rowCount = sheet.getLastRowNum();
        for (int i = 1; i < rowCount; i++) {
            Row row = sheet.getRow(i);
            if (row.getCell(0) != null && "小计".equals(row.getCell(0).getStringCellValue())) {
                return;
            }

            if (!"工业园辅助材料库".equals(row.getCell(1).getStringCellValue())) {

                Date d = row.getCell(2).getDateCellValue();
                if (d.after(start) && d.before(end)) {
                    items.add(new Item(
                            row.getCell(13).getStringCellValue(),
                            row.getCell(14) == null ? null : row.getCell(14).getStringCellValue(),
                            row.getCell(16).getNumericCellValue(),
                            row.getCell(7).getStringCellValue(),
                            d));
                }
            }
        }

    }

    public static String backupFile(String path) throws IOException {
        int suf = path.lastIndexOf(".");
        String root = path.substring(0, suf);
        String suffix = path.substring(suf);
        String backupPath = root+"-备份"+suffix;
        deleteFile(backupPath);
        try(FileInputStream fileInputStream = new FileInputStream(path);
            FileOutputStream fileOutputStream = new FileOutputStream(backupPath)){
            byte[] bytes = new byte[1024];
            while (-1 != fileInputStream.read(bytes)){
                fileOutputStream.write(bytes);
            }
            fileOutputStream.flush();
        }
        return backupPath;
    }

    public static boolean deleteFile(String path) {
        File file = new File(path);
        return file.delete();
    }
}
