package mdatool.gui.configuration;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import java.awt.Dimension;
import javax.swing.JFrame;

   public class UserPreferences{
	  private Preferences userPrefs;

	  public UserPreferences(){
		userPrefs = Preferences.userNodeForPackage(UserPreferences.class);
	  }
 
	  public Dimension getCanvasSize(){
		int width = userPrefs.getInt("canvaswidth", 100);
		int height = userPrefs.getInt("canvasheight", 200);
		return new Dimension(width, height);
	  }

	  public void putCanvasSize( Dimension dimension){
		userPrefs.putInt("canvaswidth", (int)dimension.getWidth());
		userPrefs.putInt("canvasheight", (int)dimension.getHeight());
	  }
	  
	 public void putAutomaticUmlResize(boolean yesNo){
	 	userPrefs.putBoolean("AutomaticUmlResize",yesNo); 
	 }
	 public boolean getAutomaticUmlResize(){
	 	return userPrefs.getBoolean("AutomaticUmlResize", false);
	 }
	 public String getLookAndFeel(){ 
		return userPrefs.get("lookandfeel", "windows");
	 }
	 public void putLookAndFeel(String lookAndFeel){
		userPrefs.put("lookandfeel", lookAndFeel);
	 }
	 public void putCanvasGridDistance(int dist){
		userPrefs.putInt("canvasgriddist", dist);
	 }
	 public int getCanvasGridDistance(){
	 	return userPrefs.getInt("canvasgriddist",0);
	 }
	 public void putClassColor(int color){
		userPrefs.putInt("classcolor",color);
	 }
	 public int getClassColor(){
	 	return userPrefs.getInt("classcolor",100);
	 }
	public void putClassTextColor(int color){
	   userPrefs.putInt("classtextcolor",color);
	}
	public int getClassTextColor(){
	   return userPrefs.getInt("classtextcolor",100);
	}
	  
	public void clearPreferences(){
		try {
		  userPrefs.clear();
		} catch (BackingStoreException e) {
		  e.printStackTrace();
		}
	}
   }
