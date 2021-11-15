import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class Main {
	public static void main(String[] args) throws IOException {
		XSSFWorkbook workbook = new XSSFWorkbook();
	    XSSFSheet KPsheet = workbook.createSheet("KP");
	    XSSFSheet MKPsheet = workbook.createSheet("MKP");
	    XSSFFont headerFont = workbook.createFont();
	    String[] columns = {"Tipo", "Instancia", "Experimento", "Óptimo", "Fitness", "Diferencia", "Tiempo", "", "","Fitness max","Fitness min", "Fitness avg", "Fitness dev", "Fitness range", "Time avg"};
	    headerFont.setBold(true);
	    headerFont.setFontHeightInPoints((short)12);
	    headerFont.setColor(IndexedColors.BLACK.index);
	    CellStyle headerStyle = workbook.createCellStyle();
	    headerStyle.setFont(headerFont);
	    headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
	    headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.index);
	    Row KPheaderRow = KPsheet.createRow(0);
	    Row MKPheaderRow = MKPsheet.createRow(0);
	    for(int j = 0; j < columns.length; j++) {
	    	Cell KPcell = KPheaderRow.createCell(j);
	    	Cell MKPcell = MKPheaderRow.createCell(j);
	    	KPcell.setCellValue(columns[j]);
	    	KPcell.setCellStyle(headerStyle);
	    	MKPcell.setCellValue(columns[j]);
	    	MKPcell.setCellStyle(headerStyle);
	    	KPsheet.autoSizeColumn(j);
	    	MKPsheet.autoSizeColumn(j);
	    }
	    File excel = new File("C:\\Users\\rudol\\eclipse-workspace\\BSO\\Data.xlsx");
	    FileOutputStream fos = new FileOutputStream(excel);
	    workbook.write(fos);
	    fos.close();
	    workbook.close();
		try{
	      for(int j = 0; j <= 1; j++){
	    	Problem.getInstance().type = j;
	        int ex = 1;
	    	String problem;
	        if(j == 0) {
	        	problem = "KP"; 
	        }
	        else {
	        	problem = "MKP";
	        }
	        for(int i = 0; i < 1; i++){
			  Problem.getInstance().id = i;
	          for(int k = 1; k <= 30; k++){
	            System.out.printf("\nResolviendo problema tipo %s instancia %s experimento %s\n", problem, i, k);
	            (new Swarm()).execute(problem,i,k,ex);
	            ex++;
	          }
	        }
	      }
	    }catch(Exception e) {
	      System.err.println(
	        String.format("%s \n%s", e.getMessage(), e.getCause()));
		}
	}
}
