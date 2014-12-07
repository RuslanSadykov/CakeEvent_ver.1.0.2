package bean;

import data.Model;
import jCMPL.*;
import org.apache.log4j.Logger;
import xmlData.*;

import java.io.Serializable;
import java.text.DateFormat;
import java.util.Calendar;

/**
 * Diese Bean Klasse organisiert die Sachlogik der WebAnwendung
 * Bei allen Datenzugriffen wird auf die Klasse data.Model zugegriffen
 * F�r die Probleml�sung wird auf die Klasse bean.Solver zugegriffen. 
 * @author Christian
 *
 */
public class EDbean implements Serializable{

	/**
	 * Url des verwendeten WebService
	 */
	//public static final String 	WebService	= "http://127.0.0.1";
	public static final String 	WebService	= "http://194.95.44.187:8008";
	//public static final String 	WebService	= "http://www.tfh-wildau.de/";
	//public static final String 	WebService	= "http://194.95.44.187:8009";

	/**
	 * Absoluter Verzeichnisname des Verzeichnisses in dem die ModellDateien gespeichert werden
	 */
	public static final String ModelDir 		= "I:/MyProjects/uitest/testDir/";
	//public static final String ModelDir 		= "/home/uid/or_model/cakeEvent/";

	public static final String CmplModel 		= "I:/MyProjects/uitest/cmpl/CakeEvent.cmpl";
	//public static final String CmplModel 		= "/home/uid/or_model/cakeEvent/cakeEvent.cmpl";

	/**
	 * Als Produktionssystem wird der Nutzername von
	 * der WebApplicationEngine abgefragt, �berpr�ft und der 
	 * Anwendung �bergeben.
	 */
	public static final boolean ProduktionSyst	= false;
	
	private static final long serialVersionUID = 1L;
	
	static Logger logger = Logger.getLogger(EDbean.class);

	
	/**
	 * Name des aktuellen Nutzers
	 */
	private String 	nutzer;
	/**
	 * Das aktuelle Modell
	 */
	private String  modelId;
	
	private CakeEvent		modelData;
	
	/**
	 * Initialisierung der Webanwendung,
	 * wird vom Controller aufgerufen
	 * setzt EDbean.parameterFile, Proxy Daten, ...
	 * @throws Exception 
	 */
	public static void init(String servletPath) throws Exception {
		Model.setBasisPfad(EDbean.ModelDir);
		Model.init();
		if(! EDbean.checkWebService()) throw new Exception("Webservice nicht gefunden, siehe log.");
		

	}
	
	public EDbean(){
		this.nutzer	= "Gast";
	}
	
	public void setNutzer(String nutzer){
		this.nutzer	= nutzer;
	}
	
	public String getNutzer(){
		return this.nutzer;
	}
	
	public void setModel(String modelId){
		//System.out.println("EDbean:setModel  modelId:"+modelId);
		this.modelId		= modelId;
		this.modelData		= this.getModel().getModelData();
	}
	
	public Model getModel(){
		return Model.get(this.modelId);
	}
	
	
	public String getDatum(){
		Calendar today = Calendar.getInstance();
		DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM);
		return df.format(today.getTime());
	}
	
	public String getModelName(){
		return this.modelData.getName();
	}
	
	public void setModelName(String name){
		this.modelData.setName(name);
	}
	
	public boolean isSolved(){
		return this.modelData.getSolution().size()>0 && this.modelData.isSolved();
	}
	
	public String getSolutionStatus(int solId){
		return this.modelData.getSolution().get(solId).getStatus();
	}
	
	public int getNrZutaten(){
		return this.modelData.getIngredients().getIngredient().size();
	}
	
	public int getNrRezepte(){
		return this.modelData.getRecipes().getRecipe().size();
	}
	
	public int getNrZutaten(int rezeptId){
		return this.modelData.getRecipes().getRecipe().get(rezeptId).getRecipeIngredient().size();
	}
	
	public int getNrLosungen(){
		return this.modelData.getSolution().size();
	}
	
	/**
	 * liefert HTML String f�r ZutatenTableau
	 * @return
	 */
	public String getZutatenTableau(){
		String out = "";
		out += "<table border=\"1\">\n";
		out += "<tr><th colspan=\"5\">Zutaten</th></tr>\n";
		out += "<tr><th>Id</th><th>Name</th><th>Preis [&euro;/Einheit]</th><th>Lager</th><th>Einheit</th></tr>\n";
		for (int i=0; i< this.getNrZutaten(); i++ ){
			out += "<tr>";
			out += "<td>"+this.modelData.getIngredients().getIngredient().get(i).getId()+"</td>";
			out += "<td>"+this.modelData.getIngredients().getIngredient().get(i).getName()+"</td>";
			out += "<td>"+this.modelData.getIngredients().getIngredient().get(i).getPrice()+"</td>";
			out += "<td>"+this.modelData.getIngredients().getIngredient().get(i).getStock()+"</td>";
			out += "<td>"+this.modelData.getIngredients().getIngredient().get(i).getUnit()+"</td>";
			out += "</tr>\n";
		}
		out += "</table>\n";
		return out;
	}
	
	/**
	 * liefert HTML String f�r RezepteTableau
	 * @return
	 */
	public String getRezepteTableau(){
		String out = "";
		out += "<table border=\"1\">\n";
		out += "<tr><th colspan=\"4\">Rezepte</th></tr>\n";
		for (int i=0; i< this.getNrRezepte(); i++ ){
			Recipe recipe = this.modelData.getRecipes().getRecipe().get(i);
			out += "<tr><td colspan=\"4\">";
			out += "<b>ID: </b>"+recipe.getId()+"<br/>";
			out += "<b>Name: </b>"+recipe.getName()+"<br/>";
			out += "<b>Verkaufspreis: </b>"+recipe.getSalesPrice()+" &euro;<br/>";
			out += "<b>Minimale Produktion: </b>"+recipe.getProductionLwb()+" Stck<br/>";
			out += "<b>Maximale Produktion: </b>"+recipe.getProductionUpb()+" Stck<br/>";
			out += "<b>Beschreibung:</b><br/>"+recipe.getDescription()+"<br/>";
			out += "<b>Zutaten:</b>";
			out += "</td></tr>\n";
			out += "<tr><th>Id</th><th>Name</th><th>Menge</th><th>Einheit</th></tr>\n";
			for(int j=0; j< this.getNrZutaten(i); j++){
				out += "<tr>";
				int zutatenId = recipe.getRecipeIngredient().get(j).getIngredientId(); 
				out += "<td>"+zutatenId+"</td>";
				out += "<td>"+this.modelData.getIngredients().getIngredient().get(zutatenId).getName()+"</td>";
				out += "<td>"+recipe.getRecipeIngredient().get(j).getAmount()+"</td>";
				out += "<td>"+this.modelData.getIngredients().getIngredient().get(zutatenId).getUnit()+"</td>";
				out += "</tr>\n";
			}
			out += "<tr><td colspan=\"4\"><b>Anleitung:</b><br/>"+recipe.getInstructions()+"</td></tr>\n";
		}
		out += "</table>\n";
		return out;
	}
	
	public String getSolutionTableau(){
		String out = "";
		out += "<table border=\"1\">\n";
		out += "<tr><th colspan=\"7\">L�sung</th></tr>\n";
		out += "<tr><th align=\"left\" colspan=\"5\">Solver Status: </th><td colspan=\"2\">"+this.modelData.getSolverStatus()+"</td></tr>\n";
		if(this.isSolved()){
			for (int i=0; i< this.getNrLosungen(); i++ ){
				Solution solution = this.modelData.getSolution().get(i);
				out += "<tr><th align=\"left\" colspan=\"5\">Solution Id: </th><td colspan=\"2\">"+i+"</td></tr>\n";
				out += "<tr><th align=\"left\" colspan=\"5\">Optimal: </th><td colspan=\"2\">"+solution.isOptimal()+"</td></tr>\n";
				out += "<tr><th align=\"left\" colspan=\"5\">Status: </th><td colspan=\"2\">"+solution.getStatus()+"</td></tr>\n";
				out += "<tr><th align=\"left\" colspan=\"5\">Datum der L�sung: </th><td colspan=\"2\">"+solution.getSolvedAt()+"</td></tr>\n";
				out += "<tr><th align=\"left\" colspan=\"5\">Profit:</th><td colspan=\"2\">"+solution.getProfit()+" &euro;</td></tr>\n";
				out += "<tr><th align=\"left\" colspan=\"5\">Eingesetztes Kapital:</th><td colspan=\"2\">"+solution.getUsedCapital()+" &euro;</td></tr>\n";
				out += "<tr><th align=\"left\" colspan=\"7\">Produktions-Programm:</th></tr>\n";
				out += "<tr><th colspan=\"1\">Id</th><th colspan=\"4\">Name</th><th colspan=\"2\">Menge [Stck]</th></tr>\n";
				for(int j=0; j< solution.getProduction().size(); j++){
					out += "<tr>";
					int rezeptId = solution.getProduction().get(j).getRecipeId();
					out += "<td colspan=\"1\">"+rezeptId+"</td>";
					out += "<td colspan=\"4\">"+this.modelData.getRecipes().getRecipe().get(rezeptId).getName()+"</td>";
					out += "<td colspan=\"2\">"+solution.getProduction().get(j).getQuantity()+"</td>";
					out += "</tr>\n";
				}
				out += "<tr><th align=\"left\" colspan=\"7\">Ben�tigte Zutaten:</th></tr>\n";
				out += "<tr><th>Id</th><th>Name</th><th>Lager-Menge</th><th>EK-Menge</th><th>Einheit</th><th>Kosten [&euro;]</th><th>Anteil Kosten [%]</th></tr>\n";
				for(int j=0; j< solution.getUsedIngredient().size(); j++){
					out += "<tr>";
					int zutatId = solution.getUsedIngredient().get(j).getIngredientId(); 
					out += "<td>"+zutatId+"</td>";
					out += "<td>"+this.modelData.getIngredients().getIngredient().get(zutatId).getName()+"</td>";
					out += "<td>"+solution.getUsedIngredient().get(j).getAmountStock()+"</td>";
					out += "<td>"+solution.getUsedIngredient().get(j).getAmountBuyed()+"</td>";
					out += "<td>"+this.modelData.getIngredients().getIngredient().get(zutatId).getUnit()+"</td>";
					out += "<td>"+solution.getUsedIngredient().get(j).getCostBuyed()+"</td>";
					out += "<td>"+solution.getUsedIngredient().get(j).getPercentOfCostBuyed()+"</td>";
					out += "</tr>\n";
				}
			}
		}
		out += "</table>\n";
		return out;
	}
	
	public String getHiddenModdelId(){
		String out = "<input type=\"hidden\" name=\"modelId\" value=\""+this.modelData.getId()+"\">";
		return out;
	}
	
	
	/**
	 * lieftert den HTML String zur Modell-�bersicht
	 * @return
	 */
	public String getModelsOverview(){
		String[] ids = Model.getModelIds();
		String out = "";
		out += "<table border=\"1\">\n";
		out += "<tr><th>Id</th><th>Name</th><th colspan=\"3\">Model Management</th></tr>\n";
		for(int i=0; i<ids.length; i++){
			String refModel = "Controller?action=03_showModel&modelId="+ids[i];
			out += "<tr><td>"+ids[i]+"</td>";
			out += "<td><a href=\""+refModel+"\">"+Model.get(ids[i]).getModelData().getName()+"</a></td>";
			refModel = "Controller?action=15_removeModel&modelId="+ids[i];
			out += "<td><a href=\""+refModel+"\">delete</a></td>\n";
			refModel = "Controller?action=11_saveModel&modelId="+ids[i];
			out += "<td><a href=\""+refModel+"\">save</a></td></tr>\n";
		}
		out += "</table>\n";
		return out;
	}
	
	/**
	 * liefert den HTML String zum Hinzuf�gen eines Modells
	 * @return
	 */
	public String getModelAddView(){
		String out = "";
		out	+= "<table border=\"1\">\n";
		//out += "<tr><th colspan=\"4\">Add a Model</th></tr>";
		out += "<tr><th>Model Id</th><th>Model Name</th></tr>";
		out += "<tr>";
		out += "<td><input type=\"text\" name=\"modelId\" size=\"10\" ></td>";
		out += "<td><input type=\"text\" name=\"modelName\" size=\"10\" ></td>";
		out += "</tr>";
		out	+= "</table>\n";
		return out;
	}
	
	public void resetSolution(){
		this.modelData.getSolution().clear();
		this.modelData.setSolved(false);
		this.modelData.setSolverStatus("unsolved");
	}
	
	/**
	 * speichert das Model als xml Datei
	 */
	public void save(){
		this.getModel().printDoc();
	}
	
	/**
	 * l�st das Modell
	 * Dazu werden die Modelldaten an den Solver �bergeben.
	 * Nach der L�sung werden die L�sungsdaten in die Klasse data.model
	 * �bertragen
	 */
	public void solve(){
		
		ObjectFactory	factory = new ObjectFactory();

		boolean ok = false;
		Cmpl m;
		CmplSet prod, ingr;
		CmplParameter sales_price, purchase_price, stock, v, capital, lwbProd, upbProd; 
		String[] prodVal 			= new String[this.getNrRezepte()];
		double[] sales_priceVal 	= new double[this.getNrRezepte()];
		double[] lwbProdVal 		= new double[this.getNrRezepte()];
		double[] upbProdVal 		= new double[this.getNrRezepte()];
		for(int i=0; i<this.getNrRezepte(); i++){
			prodVal[i]			= this.modelData.getRecipes().getRecipe().get(i).getName();
			prodVal[i]			= prodVal[i].trim().replace(' ', '_');
			sales_priceVal[i]	= this.modelData.getRecipes().getRecipe().get(i).getSalesPrice();
			lwbProdVal[i]		= this.modelData.getRecipes().getRecipe().get(i).getProductionLwb();
			if(lwbProdVal[i] == Double.NEGATIVE_INFINITY) lwbProdVal[i] = -1000000.0;
			upbProdVal[i]		= this.modelData.getRecipes().getRecipe().get(i).getProductionUpb();
			if(upbProdVal[i] == Double.POSITIVE_INFINITY) upbProdVal[i] = 1000000.0;
			//System.out.println(i+": "+lwbProdVal[i]+".."+upbProdVal[i]);
		}

		String[] ingrVal 			= new String[this.getNrZutaten()];
		double[] purchase_priceVal 	= new double[this.getNrZutaten()];
		double[] stockVal 			= new double[this.getNrZutaten()];
		for(int j=0; j<this.getNrZutaten(); j++){
			ingrVal[j]				= this.modelData.getIngredients().getIngredient().get(j).getName();
			ingrVal[j]				= ingrVal[j].trim().replace(' ', '_');
			purchase_priceVal[j]	= this.modelData.getIngredients().getIngredient().get(j).getPrice();
			stockVal[j]				= this.modelData.getIngredients().getIngredient().get(j).getStock();
		}
		double[][] vVal 			= new double[this.getNrRezepte()][this.getNrZutaten()];
		for(int i=0; i<this.getNrRezepte(); i++){
			for(int j=0; j<this.getNrZutaten(); j++){
				vVal[i][j]	= 0.0;
			}
			for(int l=0; l<this.modelData.getRecipes().getRecipe().get(i).getRecipeIngredient().size(); l++){
				int j 		= this.modelData.getRecipes().getRecipe().get(i).getRecipeIngredient().get(l).getIngredientId();
				vVal[i][j]	= this.modelData.getRecipes().getRecipe().get(i).getRecipeIngredient().get(l).getAmount();
			}
		}
		double capitalVal 			= this.modelData.getCapital();
		try{
			m 		= new Cmpl(EDbean.CmplModel);
			
			prod	= new CmplSet("PROD");
			prod.setValues(prodVal);
			
			ingr	= new CmplSet("INGR");
			ingr.setValues(ingrVal);
			
			sales_price 	= new CmplParameter("sales_price", prod);
			sales_price.setValues(sales_priceVal);
			
			purchase_price 	= new CmplParameter("purchase_price", ingr);
			purchase_price.setValues(purchase_priceVal);
			
			stock 	= new CmplParameter("stock", ingr);
			stock.setValues(stockVal);
			
			v 	= new CmplParameter("v", prod, ingr);
			v.setValues(vVal);
			
			capital 	= new CmplParameter("capital");
			capital.setValues(capitalVal);
			
			lwbProd 	= new CmplParameter("lwbProd", prod);
			lwbProd.setValues(lwbProdVal);
			
			upbProd 	= new CmplParameter("upbProd", prod);
			upbProd.setValues(upbProdVal);
			
			m.setSets(prod, ingr);
			m.setParameters(sales_price, purchase_price, stock, v, capital, lwbProd, upbProd);
			
			
			m.connect(EDbean.WebService);
			m.solve();
			
			if (m.solverStatus()==Cmpl.SOLVER_OK && m.nrOfSolutions()>0) {
				//System.out.println("No of Solutions: "+m.nrOfSolutions()+"   "+m.solutionPool().size());
				ok = true;
				this.modelData.setSolved(ok);
				this.modelData.setSolverStatus(m.solverStatusText());
				this.modelData.getSolution().clear();
				
				CmplSolution cmplsolution = m.solution();
				Solution solution = factory.createSolution();
				solution.setSolved(ok);
				solution.setSolvedAt(this.getModel().getTime());
				solution.setOptimal(cmplsolution.status().equals("optimal"));
				solution.setStatus(cmplsolution.status());
				solution.setProfit(cmplsolution.value());
				CmplSolArray x 		= (CmplSolArray) m.getVarByName("x"); 
				CmplSolArray y 		= (CmplSolArray) m.getVarByName("y"); 
				CmplSolElement cash = (CmplSolElement) m.getVarByName("cash"); 
				
				solution.setUsedCapital(((Double)capital.values())-(Double)cash.activity());
				for(int j=0; j<this.getNrRezepte(); j++){
					Production production = factory.createProduction();
					production.setRecipeId(j);
					String recipeName = ((String[])prod.values())[j];
					production.setQuantity((Double)x.get(recipeName).activity());
					solution.getProduction().add(production);
				}
				for(int j=0; j<this.getNrZutaten(); j++){
					int k = this.getNrRezepte()+j;
					UsedIngredient usedIndegrient = factory.createUsedIngredient();
					usedIndegrient.setIngredientId(j);
					String ingredientName = ((String[])ingr.values())[j];
					usedIndegrient.setAmountBuyed((Double)y.get(ingredientName).activity());
					usedIndegrient.setAmountStock(stockVal[j]);
					double preis 	= this.modelData.getIngredients().getIngredient().get(j).getPrice();
					usedIndegrient.setCostBuyed(usedIndegrient.getAmountBuyed() * preis);
					double anteil = (100.0 * usedIndegrient.getCostBuyed()) / solution.getUsedCapital();
					usedIndegrient.setPercentOfCostBuyed(anteil);
					solution.getUsedIngredient().add(usedIndegrient);
				}
				this.modelData.getSolution().add(solution);

				/*
				System.out.print(m.solution().status()+"\t");
				System.out.println(m.solution().value());
				for(int i=0; i<m.nrOfVariables(); i++){
					System.out.print(m.solution().variables().get(i).name()+"\t");
					System.out.println(m.solution().variables().get(i).activity());
				}
				*/
			}else{
				ok = false; 
				this.modelData.setSolved(ok);
				this.modelData.setSolverStatus(m.solverStatusText());
				System.out.println("ErrorStatus: "+m.solverStatusText());
			}
		}catch(CmplException e){
			ok = false; 
			System.out.println("CmplException  "+e.toString());
		}

	}
	
	/**
	 * f�gt ein neues Modell hinzu
	 * @param modelId
	 * @param ModelName
	 * @param nrVariables
	 * @param nrConstraints
	 */
	public void addModel(String modelId, String ModelName){
		int anzVariables, anzConstraints;
		try{
			Model model = new Model(modelId);
			model.getModelData().setName(ModelName);
			Model.add(modelId, model);
		}catch(NumberFormatException e){
			System.out.println("Fehler Modell konnte nicht angelegt werden");
		}
	}
	
	/**
	 * entfernt ein Modell
	 * @param modelId
	 */
	public void removeModel(String modelId){
		Model.remove(modelId);
	}
	
	private static boolean checkWebService(){
		boolean ok = true;
		try{
			Cmpl m = new Cmpl(EDbean.CmplModel);
			m.connect(EDbean.WebService);
		}catch(CmplException e){
			ok = false;
			logger.error("Cmplservice nicht erreichbar "+ EDbean.WebService);
		}
		return ok;

	}
}
