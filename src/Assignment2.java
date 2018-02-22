import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.imgcodecs.*;
import org.opencv.imgproc.Imgproc;

public class Assignment2 {
	public static BufferedImage Mat2BufferedImage(Mat m) {

		int type = BufferedImage.TYPE_BYTE_GRAY;
		if (m.channels() > 1) {
			type = BufferedImage.TYPE_3BYTE_BGR;
		}
		int bufferSize = m.channels() * m.cols() * m.rows();
		byte[] b = new byte[bufferSize];
		m.get(0, 0, b); // get all the pixels
		BufferedImage image = new BufferedImage(m.cols(), m.rows(), type);
		final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
		System.arraycopy(b, 0, targetPixels, 0, b.length);
		return image;

	}

	public static void displayImage(String title, Image img2) {
		ImageIcon icon = new ImageIcon(img2);
		JFrame frame = new JFrame();
		frame.setLayout(new FlowLayout());
		frame.setSize(img2.getWidth(null) + 50, img2.getHeight(null) + 50);
		frame.setTitle(title);
		JLabel lbl = new JLabel();
		lbl.setIcon(icon);
		frame.add(lbl);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

	}

	public static void DrawHisto() {

	}

	public static Mat CalcHistogram(Mat m) {

		// System.out.println(m.dump());
		List<Mat> image = new ArrayList<Mat>();
		image.add(m);

		MatOfInt channels = new MatOfInt(0);

		// Mat mask = Mat.zeros(3, 3, CvType.CV_8UC1);

		Mat hist = new Mat();

		MatOfInt histSize = new MatOfInt(256);

		float range[] = { 0, 256 };
		MatOfFloat ranges = new MatOfFloat(range);

		Imgproc.calcHist(image, channels, new Mat(), hist, histSize, ranges);
		// System.out.println(hist.dump());

		int height = 512;
		int width = 1024;
		int bin_w = (int) ((double) height / 256);
		Mat histImage = new Mat(height, width, Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
		Core.normalize(hist, hist);
		System.out.println(hist.dump());
		return hist;

	}

	public static void main(String[] args) {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		Mat cameraman = Imgcodecs.imread("cameraman.png", Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
		Mat cameraman_after = CalcHistogram(cameraman);
		displayImage("", Mat2BufferedImage(cameraman_after));

	}
}
