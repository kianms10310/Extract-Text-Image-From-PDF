

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.io.RandomAccessFile;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.text.PDFTextStripper;

public class ExtractPDF {

	public static void main(String[] args) {
		
		File pdf = new File("./pdf/00bdee6a-bacf-494f-bf76-dbbb4b11dbe3.pdf");
		
		// Pdfbox 사용하기 (첫 글자 깨질 수 있음, 글자 Font를 못 가져올 수 있음. 단 구분 가능)
		System.out.println(getContentUsePdfbox(pdf));

		// 이미지 추출하기(PDF BOX 이용)
		extractImageFromPdf(pdf);
	}
	
	static public void extractImageFromPdf(File file) {
		PDDocument document = null;
		ExtractImageFromPdf imageParser = new ExtractImageFromPdf("./image/");
		try {
			document = PDDocument.load(file);
			
			for (PDPage page : document.getPages()) {
				// Extract Image from PDF
				imageParser.processPage(page);
			}
			String uuid = UUID.randomUUID().toString();
			File imageFile = null;
			
			imageFile = new File("./image/" + uuid +".png");
			ImageIO.write(imageParser.imageStore, "PNG", imageFile);
		}catch(Exception e) {
			
		}
	}

	
	static public String getContentUsePdfbox(File file) {
		PDFTextStripper pdfStripper = null;
		PDDocument pdDoc = null;
		COSDocument cosDoc = null;
		RandomAccessFile randomAccessFile = null;
		
		try {
			randomAccessFile = new RandomAccessFile(file, "r");
			org.apache.pdfbox.pdfparser.PDFParser parser = new org.apache.pdfbox.pdfparser.PDFParser(randomAccessFile);
			parser.parse();
            cosDoc = parser.getDocument();
            pdfStripper = new PDFTextStripper();
            pdDoc = new PDDocument(cosDoc);
            return pdfStripper.getText(pdDoc);
		}catch(Exception e) {
			
		}finally {
			try {
				if(randomAccessFile != null)
					randomAccessFile.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				if(cosDoc != null)
					cosDoc.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}
}
