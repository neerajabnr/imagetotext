package it.sella.f24.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;


public class WritetoExcel {
	
	private static HSSFWorkbook workbook = null;
	private static HSSFSheet sheet = null;
	private static FileInputStream file = null;
	private static HSSFRow headerRow =null;
	
	
	static{
		try {
			file = new FileInputStream(new File("Training_Details.xls"));
			workbook = new HSSFWorkbook(file);
			sheet = workbook.getSheet("Training_Details");
			headerRow= sheet.getRow(0);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
	}
	public static void main(String[] args) {



		String colName = "TemplatePath";
		
		WritetoExcel excel=new WritetoExcel();
		String colNum = excel.getColumnData(colName);
		System.out.println(colNum);

		int rowNum = sheet.getLastRowNum();
		System.out.println(rowNum);
		rowNum++;
		HSSFRow newRow = sheet.createRow(rowNum);

		Cell cell2 = newRow.createCell(3);
		cell2.setCellValue("Neeraja");
		
		
		colNum=colNum+2;
		Cell cell3 = newRow.createCell(4);
		cell3.setCellValue("Tejasree");
		
		try {
			// this Writes the workbook gfgcontribute
			FileOutputStream out = new FileOutputStream(new File("Training_Details.xls"));
			workbook.write(out);
			out.close();
			System.out.println("Training_Details.xls written successfully on disk.");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public  String getColumnData(String colName) {
		int colNum=-1,rowNum=-1;
		for (int i = 0; i < headerRow.getLastCellNum(); i++) {
			if (headerRow.getCell(i).getStringCellValue().trim().equals(colName)) {
				colNum=i;
			} else {
				continue;
			}
		}
		
		HSSFRow row = sheet.getRow(sheet.getLastRowNum());
		HSSFCell cell = row.getCell(colNum);

		return cell.getStringCellValue();
	}
}
