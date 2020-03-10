package helper;

import java.awt.image.BufferedImage;
import java.io.File;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

public class OCR {
	private final String LANGUAGE, FALSE, TRUE;
	public final String CHARACTERS_NUMBERS, CHARACTERS_NUMBERS_AND_SLASH;
	
	public OCR() {
		LANGUAGE = "eng";
		CHARACTERS_NUMBERS = "0123456789";
		CHARACTERS_NUMBERS_AND_SLASH = "0123456789/";
		FALSE = "0";
		TRUE = "1";
	}
	
	/**
	 * From: https://www.youtube.com/watch?v=aEMSxiXctPk
	 * 
	 * For number recognition settings:
	 * https://stackoverflow.com/questions/32755943/digit-recognition-with-tesseract-ocr-and-python
	 */
	public String recognize(BufferedImage image, String characters) {
		
		// creating file instance and referencing the file in its location
		/*String imagePath = System.getProperty("user.dir") + "\\images\\";
		String fileName = "a.png";
		System.out.println(imagePath + fileName);
		File imageFile = new File(imagePath + fileName);*/
		
		// creating a new tesseract instance and setting the data path that
		// references trained data and the English language library
		ITesseract instance = new Tesseract(); // JNA Interface Mapping
		
		/* ================================================================= */
		// This part is for number recognition only
		/* ================================================================= */
		// Limit the characters being seached for to numerics.
		instance.setTessVariable("tessedit_char_whitelist", characters);
		
		// Tesseract's Directed Acyclic Graph.
		// Not necessary for number recognition.
		instance.setTessVariable("load_system_dawg", FALSE);
		instance.setTessVariable("load_freq_dawg", FALSE);
		instance.setTessVariable("load_number_dawg", TRUE);

		instance.setTessVariable("classify_enable_learning", FALSE);
		instance.setTessVariable("classify_enable_adaptive_matcher", FALSE);
		/* ================================================================= */
	    
		String dataPath = System.getProperty("user.dir") + "/data/tessdata";
		instance.setDatapath(dataPath);
		
		// create a try catch to run the OCR on te document referenced above
		try {
			//String result = instance.doOCR(imageFile);
			String result = instance.doOCR(image);
			return result;
		
		// catch that delivers an error message if OCR fails
		} catch(TesseractException e) {
			System.err.println(e.getMessage());
			return null;
		}
	}
}
