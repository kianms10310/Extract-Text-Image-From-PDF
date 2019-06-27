

import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.apache.pdfbox.contentstream.PDFStreamEngine;
import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSString;
import org.apache.pdfbox.filter.MissingImageReaderException;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

public class ExtractImageFromPdf extends PDFStreamEngine {
	private String filePath;
	String text = "";
	BufferedImage imageStore;
	
	
	public ExtractImageFromPdf(String filePath) {
		this.filePath = filePath;
	}
	
	@Override
	protected void processOperator(Operator operator, List<COSBase> operands) throws IOException {
		// TODO Auto-generated method stub
		String operation = operator.getName();
		PDXObject xobject = null;
		PDImageXObject image = null;
		
		if ("Tj".equals(operation)) {
			for (int i = 0; i < operands.size(); i++) {
				COSString base = (COSString) operands.get(i);
				if((new String(base.getBytes(), "UTF-8"))!=null) {
					text += new String(base.getBytes(), "UTF-8");
				}
				
			}
		}
		
		// "Do" 가 이미지를 나타냄.
		if ("Do".equals(operation)) {
			COSName objectName = (COSName) operands.get(0);
			try {
				xobject = getResources().getXObject(objectName);

				if (xobject instanceof PDImageXObject) {
					image = (PDImageXObject) xobject;
					int imageWidth = image.getWidth();
					int imageHeight = image.getHeight();

					BufferedImage bImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_3BYTE_BGR);

					ColorConvertOp op = new ColorConvertOp(null);
					op.filter(image.getImage(), bImage);

					
					if(imageStore != null) {
						String uuid = UUID.randomUUID().toString();
						File imageFile = null;
						if(text.contains("Fig.")) {
							imageFile = new File(this.filePath + uuid + "_" + text.substring(0, 10).replaceAll("\\.", "") +".png");
							
						}else {
							imageFile = new File(this.filePath + uuid +".png");
						}
						ImageIO.write(imageStore, "PNG", imageFile);
						text="";
					}
					
					imageStore = bImage;
				} else if (xobject instanceof PDFormXObject) {
					PDFormXObject form = (PDFormXObject) xobject;
					showForm(form);
				}
			} catch (MissingImageReaderException e) {
			}
		} else {
			super.processOperator(operator, operands);
		}
	}
	
}
