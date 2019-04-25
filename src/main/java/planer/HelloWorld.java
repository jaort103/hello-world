package planer;

import java.io.IOException;
import java.util.Random;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

public class HelloWorld {
	
	static double[] wohnungsAnteile;
	static int[] putzWochen;
	static Person[] Personen;

	public static void main(String[] args) {
		if (args.length < 3) {
			System.err.println("Falscher Aufruf: Filename.pdf Personenanzahl Wochenanzahl PersonName1 QuadratMeter1 p2 qm2 ...");
		}
		
		PDDocument document = new PDDocument();
		PDPage my_page = new PDPage();
		PDPageContentStream contentStream = null;
		
		String dateiName = args[0];
		int personenZahl = Integer.parseInt(args[1]);
		int wochenZahl = Integer.parseInt(args[2]);
		wohnungsAnteile = new double[personenZahl];
		putzWochen = new int[personenZahl];
		Personen = new Person[personenZahl];
		
		int zähler = 0;
		int woche = 0;
		
		for (int i = 3; i <= 2 * personenZahl + 1; i = i + 2, zähler++){
			Personen[zähler] = new Person(args[i], Integer.parseInt(args[i + 1]));
			//Array mit Personen wird erstellt
		}
		
		int wohnungGroesse = groesseBerechnen(personenZahl, args);
		
		wohnungsAnteile = anteileBerechnen(wohnungGroesse, zähler, wohnungsAnteile, Personen);
		
		putzWochen = verteilungBerechnen(wohnungsAnteile, zähler, wochenZahl, putzWochen);
		
		int zufallsPerson = zufälligenWählen(personenZahl);
		
		contentStream = vorbereiten(document, my_page, contentStream);
		
		planAusgeben(zähler, woche, contentStream, Personen, putzWochen, wochenZahl, zufallsPerson);
		
		beenden(document, dateiName, contentStream);

	}
	
	public static int groesseBerechnen(int personenZahl, String[] args){
		int wohnungGroesse = 0;
		for (int i = 4; i <= 2 * personenZahl + 2; i = i + 2) {
			wohnungGroesse += Integer.parseInt(args[i]);                  
			//die Größe aller Zimmer wird addiert um die Wohnungsgröße zu bekommen
		}
		return wohnungGroesse;
	}
	
	public static double[] anteileBerechnen(int wohnungGroesse, int zähler, double[] wohnungsAnteile, Person[] Personen){
		zähler = 0;
		for (int i = 0; i < Personen.length; i++) {
			wohnungsAnteile[zähler++] = Personen[i].zimmerGroesse / (double) wohnungGroesse;      
			//anhand der Wohnungsgröße wird nun der Wohnungsanteil der einzelnen Personen berechnet
		}
		return wohnungsAnteile;
	}
	
	public static int[] verteilungBerechnen(double[] wohnungsAnteile, int zähler, int wochenZahl, int[] putzWochen){
		zähler = 0;
		for (int i = 0; i < wohnungsAnteile.length; i++) {
			putzWochen[zähler++] = (int) (wochenZahl * wohnungsAnteile[i]); 
			//die Putzverteilung unter den Personen wird berechnet (wie viele Wochen eine Person putzen muss)
		}
		return putzWochen;
	}
	
	public static int zufälligenWählen(int personenZahl){
		Random r = new Random();
		int zufallsPerson = r.nextInt(personenZahl);
		return zufallsPerson;
	}
	
	public static PDPageContentStream vorbereiten(PDDocument document, PDPage my_page, PDPageContentStream contentStream){
		document.addPage(my_page);
		try {
			contentStream = new PDPageContentStream(document, my_page);
			contentStream.setFont(PDType1Font.TIMES_ROMAN, (float) 72.0);
		    contentStream.beginText();
			contentStream.setLeading(20f);
			contentStream.newLineAtOffset(25, 725);
	} catch (IOException e) {
		System.out.println("In der Methode 'vorbereiten' ist ein Problem aufgetreten ");
		e.printStackTrace();
	}
	return contentStream;
	}
	
	public static void planAusgeben(int zähler, int woche, PDPageContentStream contentStream, Person[] Personen, int[] putzWochen, int wochenZahl, int zufallsPerson){
		try {
			contentStream.showText("WG Putzplan");
			contentStream.setFont(PDType1Font.TIMES_ROMAN, (float) 16.0);
			contentStream.newLine();

		zähler = 0;
		woche = 0;
		for (int i = 0; i < Personen.length; i++) {
			zähler += putzWochen[i];
			for (int j = 0; j < putzWochen[i]; j++) {
					
					contentStream.showText("Woche " + ++woche);
					contentStream.showText(" ");
					contentStream.showText(Personen[i].name);
					contentStream.newLine();
			}
		}
		woche++;

		if (zähler < wochenZahl)         //falls es keine gerechte Wochenverteilung geben kann
				contentStream.showText("Woche " + woche);
				contentStream.showText(" ");
				contentStream.showText(Personen[zufallsPerson].name);   //muss die Zufallsperson aus Zeile 78 die letzte Woche putzen
				contentStream.showText(" (Pech gehabt)");
				contentStream.newLine();
			} catch (IOException e1) {
				System.out.println("In der Methode 'planAusgabe' ist ein Problem aufgetreten ");
				e1.printStackTrace();
			}
	}
	
	public static void beenden(PDDocument document, String dateiName, PDPageContentStream contentStream){
		try {
			contentStream.endText();
			contentStream.close();
			document.save(dateiName);
			document.close();
		} catch (IOException e2) {
			System.out.println("In der Methode 'beenden' ist ein Problem aufgetreten ");
			e2.printStackTrace();
		}
	}

}