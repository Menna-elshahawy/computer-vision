
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.*;
import org.opencv.imgproc.Imgproc;

public class Assignment1 {

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

	public static Mat dimImage(Mat source) {
		// for every pixel in the matrix decrease by beta.
		double alpha = 1;// scale factor.
		double beta = -50;// Optional delta added to the scaled values.
		Mat destination = new Mat(source.rows(), source.cols(), source.type());
		source.convertTo(destination, -1, alpha, beta);// -1, destination mat
														// type same as source.
		return destination;
	}

	public static Mat brightenImage(Mat source) {
		// for every pixel in the matrix increase by beta.
		double alpha = 1;// scale factor.
		double beta = 200;// Optional delta added to the scaled values.
		Mat destination = new Mat(source.rows(), source.cols(), source.type());
		source.convertTo(destination, -1, alpha, beta);
		return destination;
	}

	public static Mat RemoveShadow(Mat source) {
		Mat result = new Mat();
		// Applies a fixed-level threshold to each array element.
		// # Truncate Threshold
		// if src(x,y) > thresh (170)
		// dst(x,y) = thresh (170)
		// else
		// dst(x,y) = src(x,y)

		Imgproc.threshold(source, result, 170, 255, Imgproc.THRESH_TRUNC);
		return result;
	}

	public static Mat SelectiveBrightening(Mat source) {
		// By pixel manipulation
		// pass by every pixel, if pixel is too dim (<50), add a 50 to it.
		Mat destination = new Mat(source.rows(), source.cols(), source.type());
		for (int i = 0; i < source.rows(); i++) {
			for (int j = 0; j < source.cols(); j++) {
				double[] pixel = source.get(i, j);
				if (pixel[0] < 50)
					destination.put(i, j, pixel[0] + 50);
				else
					destination.put(i, j, pixel[0]);
			}
		}
		return destination;
	}

	public static Mat segment(Mat source) {
		Mat destination = new Mat();
		// smallest value between threshold1 and threshold2 is used for edge
		// linking. The largest value is used to find initial segments of strong
		// edges.
		Imgproc.Canny(source, destination, 150, 100);
		// dilation (widening the pixels)
		Mat element1 = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(5, 5));
		Imgproc.dilate(destination, destination, element1);
		// anding both to get the desired output.
		Core.bitwise_and(source, destination, destination);
		return destination;
	}

	public static Mat combine_images(Mat M1, Mat M2) {
		M1 = shift_image(M1);
		Mat destination = new Mat(M1.rows(), M1.cols(), M1.type());
		for (int i = 0; i < M1.rows(); i++) {
			for (int j = 0; j < M1.cols(); j++) {
				double[] pixel = M1.get(i, j);
				if (!(pixel[0] >= 250)) // 255 doesn't work, maybe because
										// pixels are not totally
										// white..
					destination.put(i, j, pixel[0]);
				else
					destination.put(i, j, M2.get(i, j));

			}
		}
		return destination;
	}

	public static Mat shift_image(Mat source) {
		Mat destination = new Mat(source.rows(), source.cols(), source.type());
		for (int i = 0; i < source.rows(); i++) {
			for (int j = 200; j < source.cols(); j++) {
				destination.put(i, j - 200, source.get(i, j));
			}
		}
		// to fill the black
		for (int i = 0; i < destination.rows(); i++) {
			for (int j = 1000; j < destination.cols(); j++) {
				double[] pixel = destination.get(i, j);
				if (pixel[0] == 0)
					destination.put(i, j, 255);
			}
		}
		return destination;
	}

	public static Mat combine_image2(Mat M1, Mat M2) {
		return combine_images(flip_image(M1), M2);
	}

	public static Mat flip_image(Mat source) {
		Mat destination = new Mat();
		// last argument;0 for vertical flip,1 for horizontal.
		Core.flip(source, destination, 1);
		return destination;
	}

	public static void main(String[] args) {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

		// Load Images :
		Mat guc = Imgcodecs.imread("GUC.png", Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
		Mat calculator = Imgcodecs.imread("calculator.png", Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
		Mat coat = Imgcodecs.imread("cameraman.png", Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
		Mat lake = Imgcodecs.imread("lake.png", Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
		Mat james = Imgcodecs.imread("james.png", Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
		Mat london1 = Imgcodecs.imread("london1.png", Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
		Mat london2 = Imgcodecs.imread("london2.png", Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);

		// -------------------------------------------------------------------------

		// For the top view of the guc:

		 displayImage("GUC", Mat2BufferedImage(guc));
		 Mat guc_after = dimImage(guc);
		 displayImage("Dimmed image of GUC", Mat2BufferedImage(guc_after));

		// -------------------------------------------------------------------------
		// For the shadow of calculator:
		//
		 displayImage("Calculator with shadow",
		 Mat2BufferedImage(calculator));
		 Mat calc_before = RemoveShadow(calculator);
		 displayImage("Calculator with no shadow",
		 Mat2BufferedImage(calc_before));

		// //
		// -------------------------------------------------------------------------
		// // Cameraman’s coat:
		//
		 displayImage("Coat Before selective brightening",
		 Mat2BufferedImage(coat));
		 Mat coat_after = SelectiveBrightening(coat);
		 // System.out.println(coat.dump());//to print mat values
		 displayImage("Coat After selective brightening",
		 Mat2BufferedImage(coat_after));
		//
		// //
		// -------------------------------------------------------------------------
		// // Basic segmentation
		//
		 displayImage("Lake Before segmentation", Mat2BufferedImage(lake));
		 Mat lake_after = segment(lake);
		 displayImage("Lake After segmentation",
		 Mat2BufferedImage(lake_after));

		// //
		// -------------------------------------------------------------------------
		// // Images combination
		// // james with london1
		//
		 Mat james_london1 = combine_images(james, london1);
		 displayImage("James + London1", Mat2BufferedImage(james_london1));

		// // james with london2
		//
		 Mat james_london2 = combine_image2(james, london2);
		 displayImage("James + London2", Mat2BufferedImage(james_london2));

	}
}
