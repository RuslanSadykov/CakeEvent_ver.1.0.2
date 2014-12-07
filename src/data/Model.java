package data;

import org.apache.log4j.Logger;
import xmlData.*;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.text.DateFormat;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.Hashtable;

/**
 * Diese Klasse speichert und liest alle Modelldaten in/aus 
 * einer xml Datei Detailinformationen sind der Methode getModelData zu entnehmen.
 * Ausserdem werden alle bestehenden Modelle in einer 
 * Hashtabelle verwaltet.
 * @author Christian
 *
 */
public class Model {

	static Logger logger = Logger.getLogger(Model.class);

	/**
	 * Die relative Wurzel f�r die Modelldateien
	 */
	static String basisPfad = null;
	/**
	 * interne Hashtabelle, die jedem Modellnamen, die entsprechende Klassen-Instanz zuweist.
	 */
	static Hashtable<String, Model> modelle = null;
	
	static JAXBContext 		jaxbContext;
	static{
		try {
			jaxbContext = JAXBContext.newInstance("xmlData");
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private CakeEvent 			modelData = null;
	private String   			datei = null;
	
	/**
	 * liefert die Hashtable mit allen erzeugten Modellen
	 * @return
	 */
	public static Hashtable<String, Model> getModelle(){
		return Model.modelle;
	}
	
	public static void setBasisPfad(String pfad){
		Model.basisPfad = pfad;
	}
	
	/**
	 * einlesen aller Modell Dateien
	 */
	public static void init(){
		Model.modelle = new Hashtable<String, Model>();
		File basis = new File(Model.basisPfad);
		//System.out.println("basisPfad: "+basis.getAbsolutePath());
		if(basis.isDirectory()){
			String[] mo = basis.list();
			File mo_file = null;
			if(mo != null){
				for(int i=0; i < mo.length; i++){
					mo_file = new File(basis.getAbsolutePath()+"/"+mo[i]);
					//System.out.println("gefunden: "+mo_file.getAbsolutePath());
					if(mo_file.isFile()){
						Model m = new Model(mo_file);
						if(m == null) System.out.println("Model.init  Einlesefehler von "+mo_file.getAbsolutePath());
						Model.modelle.put(m.getId(), m);
					}
				}
			}
		}
	}
	
	/**
	 * liefert das Modell mit modelId
	 * @param modelId
	 * @return
	 */
	public static Model get(String modelId){
		return Model.modelle.get(modelId);
	}
	
	/**
	 * f�gt neues Modell mit modelId der Hashtable hinzu
	 * @param modelId
	 * @param model
	 */
	public static void add(String modelId, Model model){
		if(! Model.modelle.containsKey(modelId)){
			Model.modelle.put(modelId, model);
			model.printDoc();
		}
	}
	
	/**
	 * entfernen eines Modells
	 * @param modelId
	 */
	public static void remove(String modelId){
		if( Model.modelle.containsKey(modelId)){
			Model model = Model.modelle.get(modelId);
			Model.modelle.remove(modelId);
			File f = new File(model.getDirectory()+model.datei);
			f.delete();
			//System.out.println(model.getDirectory()+model.datei);
		}
	}
	
	/**
	 * liefert Array mit den ID's aller Modelle
	 * @return
	 */
	public static String[] getModelIds(){
		int l = Model.modelle.size();
		String[] out	= new String[l];
		Enumeration<String> e = Model.modelle.keys();
		int i=0;
		while(e.hasMoreElements()){
			out[i++] = e.nextElement();
		}
		return out;
	}

	
	/**
	 * information �ber alle erstellten Modelle
	 * @return
	 */
	public static String info(){
		String out = "";
		for(Enumeration e = Model.modelle.keys(); e.hasMoreElements(); ){
			Model model = Model.modelle.get(e.nextElement());
			out += model.modelData.getId()+"  "+model.modelData.getName();
			out += "\n";
		}
		return out;
	}
	
	/**
	 * Konstruktor zum Einlesen einer als xml-Datei abgespeicherten Modells
	 * @param modelFile	File in dem das Model gespeichert ist
	 */
	public Model(File modelFile){
		this.datei 		= modelFile.getName();
		this.modelData	= null;
		//System.out.println("Datei : "+this.datei);
		if(modelFile.isFile()){
			//System.out.println(modelFile.getAbsolutePath());
			try {
				this.modelData = this.unmarshalXml(new FileInputStream(modelFile));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Konstruktor zum Anlegen eines neuen Modells. 
	 * @param mid				ModellId
	 * @param anzVariables
	 * @param anzConstraints
	 */
	public Model(String mid){
		this.datei = mid+".xml";
		ObjectFactory factory 	= new ObjectFactory();
		// erzeugt Demo CakeEvent
		Ingredient 			ingredient;
		Recipe recipe;
		RecipeIngredient 	recipeIngredient;

		this.modelData 	= factory.createCakeEvent();
		this.modelData.setId(mid);
		this.modelData.setName(mid);
		this.modelData.setCreatedAt(this.getTime());
		this.modelData.setAutor("Christian M�ller");
		this.modelData.setDescription("OR Kuchen backen Aufgabe");
		this.modelData.setCapital(400.0);
		this.modelData.setSolved(false);
		this.modelData.setSolverStatus("unsolved");
		this.modelData.setIngredients(factory.createIngredients());
		this.modelData.setRecipes(factory.createRecipes());
		
		ingredient = factory.createIngredient();
		ingredient.setId(0);
		ingredient.setName("Mehl");
		ingredient.setUnit(Units.KG);
		ingredient.setStock(600.0);
		ingredient.setPrice(1.5);
		this.modelData.getIngredients().getIngredient().add(ingredient);
		ingredient = factory.createIngredient();
		ingredient.setId(1);
		ingredient.setName("Zucker");
		ingredient.setUnit(Units.KG);
		ingredient.setStock(250.0);
		ingredient.setPrice(2.5);
		this.modelData.getIngredients().getIngredient().add(ingredient);
		ingredient = factory.createIngredient();
		ingredient.setId(2);
		ingredient.setName("Milch");
		ingredient.setUnit(Units.LITER);
		ingredient.setStock(300.0);
		ingredient.setPrice(1.2);
		this.modelData.getIngredients().getIngredient().add(ingredient);
		ingredient = factory.createIngredient();
		ingredient.setId(3);
		ingredient.setName("Eier");
		ingredient.setUnit(Units.STCK);
		ingredient.setStock(2400.0);
		ingredient.setPrice(0.2);
		this.modelData.getIngredients().getIngredient().add(ingredient);
		
		
		recipe = factory.createRecipe();
		recipe.setId(0);
		recipe.setName("Kuchen 1");
		recipe.setDescription("Beschreibung des Kuchen 1");
		recipe.setInstructions("Anleitung zum Erstellen eines Kuchen 1");
		recipe.setSalesPrice(6.0);
		recipe.setProductionLwb(0.0);
		recipe.setProductionUpb(Double.POSITIVE_INFINITY);
		recipeIngredient = factory.createRecipeIngredient();
		recipeIngredient.setIngredientId(0);
		recipeIngredient.setAmount(2.0);
		recipe.getRecipeIngredient().add(recipeIngredient);
		recipeIngredient = factory.createRecipeIngredient();
		recipeIngredient.setIngredientId(1);
		recipeIngredient.setAmount(0.5);
		recipe.getRecipeIngredient().add(recipeIngredient);
		recipeIngredient = factory.createRecipeIngredient();
		recipeIngredient.setIngredientId(2);
		recipeIngredient.setAmount(1.0);
		recipe.getRecipeIngredient().add(recipeIngredient);
		recipeIngredient = factory.createRecipeIngredient();
		recipeIngredient.setIngredientId(3);
		recipeIngredient.setAmount(6.0);
		recipe.getRecipeIngredient().add(recipeIngredient);
		this.modelData.getRecipes().getRecipe().add(recipe);

		recipe = factory.createRecipe();
		recipe.setId(1);
		recipe.setName("Kuchen 2");
		recipe.setDescription("Beschreibung des Kuchen 2");
		recipe.setInstructions("Anleitung zum Erstellen eines Kuchen 2");
		recipe.setSalesPrice(8.0);
		recipe.setProductionLwb(0.0);
		recipe.setProductionUpb(Double.POSITIVE_INFINITY);
		recipeIngredient = factory.createRecipeIngredient();
		recipeIngredient.setIngredientId(0);
		recipeIngredient.setAmount(1.0);
		recipe.getRecipeIngredient().add(recipeIngredient);
		recipeIngredient = factory.createRecipeIngredient();
		recipeIngredient.setIngredientId(1);
		recipeIngredient.setAmount(1.0);
		recipe.getRecipeIngredient().add(recipeIngredient);
		recipeIngredient = factory.createRecipeIngredient();
		recipeIngredient.setIngredientId(2);
		recipeIngredient.setAmount(0.5);
		recipe.getRecipeIngredient().add(recipeIngredient);
		recipeIngredient = factory.createRecipeIngredient();
		recipeIngredient.setIngredientId(3);
		recipeIngredient.setAmount(9.0);
		recipe.getRecipeIngredient().add(recipeIngredient);
		this.modelData.getRecipes().getRecipe().add(recipe);
	}
	
	/**
	 * zu Test Zwecken
	 * @param args
	 */
	public static void main(String[] args) {
		Model.setBasisPfad("./testDir/");
		Model.init();
		System.out.println(Model.info());
		
		Model model = new Model("testx");
		model.printDoc();
	}
	
	/**
	 * liefert das Verzeichnis, in der die Modelle liegen
	 * 	 * @return
	 */
	public String getDirectory(){
		String out = Model.basisPfad+this.datei;
		return out.substring(0, out.lastIndexOf('/')+1);
	}
	
	/**
	 * liefert Id des Modelles
	 * @return
	 */
	public String getId(){
		return this.modelData.getId();
	}
	
	/**
	 * liefert die xmlDaten
	 * @return
	 */
	public CakeEvent getModelData(){
		return this.modelData;
	}
	
	/**
	 * gibt doc in this.datei aus
	 * schreibt ins Dateisystem
	 * @return			  true wenn alles ok
	 */
	public boolean printDoc(){
		boolean out = false;
		//System.out.println("Schreibe Problem auf: "+Model.basisPfad+this.datei);
		File file = new File(Model.basisPfad+this.datei);
		FileOutputStream f;
		try {
			f 	= 	new FileOutputStream(file);
			out =	this.marshalXml(f , modelData);
			f.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return out;
	}
	
	/**
	 * gibt die aktuelle Zeit als String
	 * @return
	 */
	public String getTime(){
		GregorianCalendar cal = new GregorianCalendar();
		DateFormat tag  = DateFormat.getDateInstance(DateFormat.MEDIUM);
		DateFormat time = DateFormat.getTimeInstance(DateFormat.MEDIUM);
		return tag.format(cal.getTime())+"  "+time.format(cal.getTime());
	}
	
	private CakeEvent unmarshalXml(InputStream xmlStream){
		CakeEvent model = null;
		try {
			Unmarshaller u = Model.jaxbContext.createUnmarshaller();
			Object paraXML = u.unmarshal(xmlStream);
			model = (CakeEvent) paraXML;
		} catch (JAXBException je) { }
		return model;
		}
	
	private boolean marshalXml(OutputStream xmlStream, CakeEvent model){
		boolean out = true;
		Marshaller m;
		try {
			m = Model.jaxbContext.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			m.setProperty(Marshaller.JAXB_NO_NAMESPACE_SCHEMA_LOCATION,"CakeEvent.xsd");
			m.marshal(model, xmlStream);
		} catch (JAXBException e) {out = false;}
		return out;
		}
}
