package server;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import javax.imageio.*;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;

public class FileManager {
	
	private static Logger logger = LogManager.getLogger(FileManager.class);
	
	public String getFileType(byte[] bytes) {
		Tika tika = new Tika();
		logger.info("getFileType");
		String type = "";
		type = tika.detect(bytes);
		logger.info("File type: " + type);
		tika = null;
		return type;
	}

	public byte[] decodeFile(String encoded) {
		byte[] decodedBytes = Base64.getMimeDecoder().decode(encoded);
		return decodedBytes;
	}

	public String saveFile(byte[] decodedBytes, String fileType) {
		String fileName = new Randomizer().getRandomName();
		String fileExtension = getExt(fileType);
		String fileNameWithExtension = fileName + "." + fileExtension;
		while(fileExists(fileNameWithExtension)) {
			logger.info("file name exists, generating new");
			fileName = new Randomizer().getRandomName();
			fileNameWithExtension = fileName + "." + fileExtension;
		}
		String url = "media/" + fileNameWithExtension;
		try {
			FileUtils.writeByteArrayToFile(new File(url), decodedBytes);
			logger.info("saved file: " + fileNameWithExtension);
		} catch (IOException e) {
			logger.error("could not save file to disk", e);
			return "-1";
		}
		return fileNameWithExtension;
	}

	private String getExt(String fileType) {
		String extension = "";
		String s = "[/]";
		String[] parts = fileType.split(s);
		extension = parts[1];
		return extension;
	}

	public boolean removeFile(String fileName) {
		Path path = Paths.get("media/" + fileName);
		try {
			logger.info("removing file " + fileName);
			Files.delete(path);
		} catch (IOException e) {
			logger.error("could not delete file " + fileName, e);
			return false;
		}
		return true;
	}
	
	public void getMp4Data(byte[] file) {
		Tika tika = new Tika();
		Metadata metaData = new Metadata();
		logger.info("getMP4Data");
		File fileD = new File("src/main/resources/testvideof.mp4");
		FileInputStream inputStream = null;
		String mimeType = "";
		
	}

	public float checkRatio(byte[] bytes) {
		float ratio = 0;
		try {
			BufferedImage image = ImageIO.read(new ByteArrayInputStream(bytes));
			int height = image.getHeight();
			int width = image.getWidth();
			logger.info("Image width: " + width);
			logger.info("Image height: " + height);
			ratio = (float) (width) / (height);
			logger.info("image ratio: " + ratio);
		} catch (IOException e) {
			logger.info("could not get image ratio", e);
		}
		return ratio;
	}

	private boolean fileExists(String fileName) {
		File file = new File("media/" + fileName);
		boolean result = file.exists();
		return result;
	}
}
