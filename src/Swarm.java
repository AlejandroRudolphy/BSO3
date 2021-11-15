import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class Swarm {
  private int nIdeas = 30;
  private final int T = 10; //Cantidad iteraciones
  private final double PReplace = 0.5; /* Probabilidad de cambiar el centro del cluster por una idea aleatoria */
  private final double PGeneration = 0.5; /* Probabilidad que define el metodo de generacion ideas entre OneCluster y TwoCluster  */
  private final double POneCluster = 0.5; /* Probabilidad que determina el como se va a generar una nueva idea cuando se usa OneCluster */
  private final double PTwoCluster = 0.5; /* Probabilidad que determina el como se va a generar una nueva idea cuando se usa TwoCluster */

  private ArrayList<Idea> swarm = null;
  private Idea g = null;

	private long sTime, eTime;

    public void execute(String problem, int i, int k, int ex) throws IOException {
	  startTime();
      init();
      run(problem, i, k, ex);
      log();
    }

	private void startTime() {
		sTime = System.currentTimeMillis();
	}

	private void init() throws IOException {
		swarm = new ArrayList<>();
	    g = new Idea();
	    Idea p = null;
	    for (int i = 0; i <= nIdeas; i++) {
	      p = new Idea();
	      if (!p.isFeasible()){ 
	       //System.out.printf("Reparando idea %s: ",i);
	       p.repare();
	      }
	      else{
	        //System.out.printf("Idea %s pasa sin problemas\n",i);
	      }
	      swarm.add(p);
	    }
	    g.copy(swarm.get(0));
	    /*for (int i = 1; i < nIdeas; i++) {
	      if (swarm.get(i).isBetterThan(g)) {
	          g.copy(swarm.get(i));
	      }
	    }*/
	    System.out.printf("Primera idea: %s\n",g);
	}

	private List<Integer> Centers(int nc){
	    List<Integer> centers = new ArrayList<Integer>();
	    List<Integer> pos = new ArrayList<Integer>();
	    int x = nIdeas/nc;
	    for(int i = 1; i <= nc; i++){
	      pos.add(x*i);
	    }
	    int y = x/2;
	    for(int j = 0; j < nc; j++){
	      centers.add(pos.get(j)-y);
	    }
	    return pos;
	  }
	
	  private List<List<Idea>> Cluster(List<Integer> pos, int nc){
	    List<List<Idea>> cSwarm = new ArrayList<>(); 
	    // [0,1,2,3]
	    for(int j = 0; j < pos.size(); j++){
	      cSwarm.add(new ArrayList<Idea>());
	      // con j = 0,
	      // pos = [9,18,27,36]
	      // [for k = 0; k < 9; k++]
	      for(int k = (pos.get(j)-(nIdeas/nc)); k < pos.get(j); k++){
	        // cSwarm.get(j) = [[aqui],[aqui no],[aqui no],[aqui no]]
	        // cSwarm.get(j).add(swam.get(k)) con k de 0 hasta 9
	        cSwarm.get(j).add(swarm.get(k));
	      } 
	    }
	    return cSwarm;
	 }
	  
	  private int NumClusters(){
		    int k = 0;
		    if(nIdeas <= 10){
		      k = 2;
		    }
		    if(nIdeas > 10 && nIdeas <= 30){
		      k = 3;
		    }
		    if(nIdeas > 30){
		      k = 4;
		    }
		    return k;
		}


	private void run(String problem, int i, int k, int ex) throws IOException {
		FileInputStream inputStream = new FileInputStream("C:\\Users\\rudol\\eclipse-workspace\\BSO\\Data.xlsx");
		XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
	    XSSFSheet KPsheet = workbook.getSheetAt(0);
	    XSSFSheet MKPsheet = workbook.getSheetAt(1);
	    String[] formulas = {"MAX(E2:E31)","MIN(E2:E31)","AVERAGE(E2:E31)","STDEV(E2:E31)","QUARTILE(E2:E31,3)-QUARTILE(E2:E31,1)","AVERAGE(G2:G31)"};
	    int numCluster = NumClusters();
	    List<Integer> pos = new ArrayList<Integer>();
	    pos = Centers(numCluster);
	    List<List<Idea>> cSwarm = new ArrayList<>();
	    cSwarm = Cluster(pos, numCluster);
	    List<Idea> cb = new ArrayList<>();
		int t = 1;
	    while (t <= T) {
    	  for(int j = 0; j < cSwarm.size(); j++){
    		  	g = new Idea();
    	        g.copy(cSwarm.get(j).get(0));
    	        for(int y = 0; y < cSwarm.get(j).size(); y++){
    	          //System.out.printf("datos cluster %s, %s\n",j,cSwarm.get(j).get(y));
    	          if (cSwarm.get(j).get(y).isBetterThan(g)) 
    	            g.copy(cSwarm.get(j).get(y));
    	        }
    	        //System.out.printf("mejor valor del cluster %s -> %s\n",j,g);
    	        //System.out.println("*******************************************************");
    	        cb.add(g);
    	  }
	      for(int y = 0; y < cb.size(); y++){
	        //System.out.printf("Cluster %s -> %s\n",y,cb.get(y));
	        int rand = StdRandom.uniform(2);
	        if(rand > PReplace) {
	        	cb.get(y).replaceCenter();
	        	//System.out.printf("Cluster post %s -> %s\n",y,cb.get(y));
	        }
	        if(cb.get(y).isBetterThan(g))
	          g.copy(cb.get(y));
	        
	      }
    	  cb = new ArrayList<>();
	      //for (int i = 0; i < nIdeas; i++) {
	        //do{
	          //  swarm.get(i).move(g, w, c1, c2);
	        //}while(!swarm.get(i).isFeasible());
	        /*swarm.get(i).move(g, w, c1, c2);
	        if (!swarm.get(i).isFeasible()) {
	          swarm.get(i).repare();
	        }*/
	    t++;   
	    }
	    eTime = System.currentTimeMillis(); 
	    if(problem == "KP") {
	    	Row row = KPsheet.createRow(ex);
	    	if(ex == 1) {
				row.createCell(0).setCellValue(problem);
				row.createCell(1).setCellValue(i);
				row.createCell(2).setCellValue(k);
				row.createCell(3).setCellValue(g.optimal());
				row.createCell(4).setCellValue(g.fitness());
				row.createCell(5).setCellValue(g.diff());
				row.createCell(6).setCellValue((eTime - sTime));
			    for(int z = 0; z < formulas.length; z++) {
		    	Cell KPcell = row.createCell(9+z);
		    	Cell MKPcell = row.createCell(9+z);
		    	KPcell.setCellFormula(formulas[z]);
		    	MKPcell.setCellFormula(formulas[z]);
		    	KPsheet.autoSizeColumn(9+z);
		    	MKPsheet.autoSizeColumn(9+z);
			    }
	    	 }
			 else {
				row.createCell(0).setCellValue(problem);
				row.createCell(1).setCellValue(i);
				row.createCell(2).setCellValue(k);
				row.createCell(3).setCellValue(g.optimal());
				row.createCell(4).setCellValue(g.fitness());
				row.createCell(5).setCellValue(g.diff());
				row.createCell(6).setCellValue((eTime - sTime)); 	
			 }
	    }
	    if(problem=="MKP") {
	    	Row row = MKPsheet.createRow(ex);
	    	if(ex == 1) {
				row.createCell(0).setCellValue(problem);
				row.createCell(1).setCellValue(i);
				row.createCell(2).setCellValue(k);
				row.createCell(3).setCellValue(g.optimal());
				row.createCell(4).setCellValue(g.fitness());
				row.createCell(5).setCellValue(g.diff());
				row.createCell(6).setCellValue((eTime - sTime));
			    for(int z = 0; z < formulas.length; z++) {
		    	Cell KPcell = row.createCell(9+z);
		    	Cell MKPcell = row.createCell(9+z);
		    	KPcell.setCellFormula(formulas[z]);
		    	MKPcell.setCellFormula(formulas[z]);
		    	KPsheet.autoSizeColumn(9+z);
		    	MKPsheet.autoSizeColumn(9+z);
			    }
	    	 }
			 else {
				row.createCell(0).setCellValue(problem);
				row.createCell(1).setCellValue(i);
				row.createCell(2).setCellValue(k);
				row.createCell(3).setCellValue(g.optimal());
				row.createCell(4).setCellValue(g.fitness());
				row.createCell(5).setCellValue(g.diff());
				row.createCell(6).setCellValue((eTime - sTime)); 	
			 }
	    }
	    
	    inputStream.close();
	    File excel = new File("C:\\Users\\rudol\\eclipse-workspace\\BSO\\Data.xlsx");
	    FileOutputStream fos = new FileOutputStream(excel);
	    workbook.write(fos);
	    fos.close();
	    workbook.close();  
	  }


    private void log() {
        StdOut.printf("Mejor idea: %s\n",g);
        //StdOut.printf("%s, s: %s, t: %s\n", g, StdRandom.getSeed(), (eTime - sTime));
    }	
}
