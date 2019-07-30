package ini.ext.events;

import static name.audet.samuel.javacv.jna.cv.CV_BGR2HSV;
import static name.audet.samuel.javacv.jna.cv.CV_HOUGH_GRADIENT;
import static name.audet.samuel.javacv.jna.cv.cvCvtColor;
import static name.audet.samuel.javacv.jna.cv.cvHoughCircles;
import static name.audet.samuel.javacv.jna.cxcore.IPL_DEPTH_8U;
import static name.audet.samuel.javacv.jna.cxcore.cvCircle;
import static name.audet.samuel.javacv.jna.cxcore.cvGetSeqElem;
import static name.audet.samuel.javacv.jna.cxcore.cvInRangeS;
import static name.audet.samuel.javacv.jna.cxcore.cvScalar;
import ini.ast.AtPredicate;
import ini.ast.Rule;
import ini.eval.IniEval;
import ini.eval.at.At;
import ini.eval.data.Data;
import ini.eval.data.RawData;

import java.util.HashMap;
import java.util.Map;

import name.audet.samuel.javacv.CanvasFrame;
import name.audet.samuel.javacv.FrameGrabber;
import name.audet.samuel.javacv.OpenCVFrameGrabber;
import name.audet.samuel.javacv.jna.cxcore.CvMemStorage;
import name.audet.samuel.javacv.jna.cxcore.CvPoint;
import name.audet.samuel.javacv.jna.cxcore.CvScalar;
import name.audet.samuel.javacv.jna.cxcore.CvSeq;
import name.audet.samuel.javacv.jna.cxcore.IplImage;

public class AtBallMove extends At {

	CanvasFrame frame, hsvFrame, thresholdedFrame, thresholdedFrame2,
			thresholdedFrame3;
	FrameGrabber grabber;
	IplImage grabbedImage, hsv_frame, thresholded;
	CvScalar hsv_min = cvScalar(0, 150, 150, 0);
	CvScalar hsv_max = cvScalar(20, 256, 256, 0);
	Map<String, Data> variables = new HashMap<String, Data>();
	Thread mainThread;

	@Override
	public void eval(final IniEval eval) {
//		if (atPredicate.configurationArguments.size() != 5) {
//			throw new RuntimeException(
//					"wrong number of arguments in @ballMove (it should have 4 arguments)");
//		}
		final Data debug = getInContext().get("showWindows");
		try {
			debug.getBoolean().booleanValue();
		} catch (Exception e) {
			throw new RuntimeException("wrong parameter '"
					+ getAtPredicate().inParameters.get(0)
					+ "' in @ballMove clause, should be a boolean", e);
		}
		final Data t = getInContext().get("sleepTime");
		try {
			t.getNumber().intValue();
		} catch (Exception e) {
			throw new RuntimeException("wrong parameter '"
					+ getAtPredicate().inParameters.get(0)
					+ "' in @ballMove clause, should be a number", e);
		}

		mainThread = new Thread() {
			@Override
			public void run() {
				try {
					if (debug.getBoolean().booleanValue()) {
						frame = new CanvasFrame("Test JavaCV");
						frame.setDefaultCloseOperation(CanvasFrame.DISPOSE_ON_CLOSE);
						frame.setBounds(320, 300, 320, 240);
						hsvFrame = new CanvasFrame("Espace de couleur HSV");
						hsvFrame.setBounds(320, 0, 320, 240);
						hsvFrame.setDefaultCloseOperation(CanvasFrame.DISPOSE_ON_CLOSE);
						thresholdedFrame = new CanvasFrame("Après filtrage n°1");
						thresholdedFrame.setBounds(640, 0, 320, 240);
						thresholdedFrame
								.setDefaultCloseOperation(CanvasFrame.DISPOSE_ON_CLOSE);
					}
					grabber = new OpenCVFrameGrabber(0);
					grabber.start();
					grabbedImage = grabber.grab();
					hsv_frame = IplImage.create(grabbedImage.width,
							grabbedImage.height, IPL_DEPTH_8U, 3);
					thresholded = IplImage.create(grabbedImage.width,
							grabbedImage.height, IPL_DEPTH_8U, 1);
					do {
						sleep(t.getNumber().intValue());
						grabbedImage = grabber.grab();
						hsv_frame = IplImage.create(grabbedImage.width,
								grabbedImage.height, IPL_DEPTH_8U, 3);
						cvCvtColor(grabbedImage, hsv_frame, CV_BGR2HSV);
						if (debug.getBoolean().booleanValue()) {
							hsvFrame.showImage(hsv_frame);
						}
						cvInRangeS(hsv_frame, hsv_min.byValue(),
								hsv_max.byValue(), thresholded);
						if (debug.getBoolean().booleanValue()) {
							thresholdedFrame.showImage(thresholded);
						}
						CvMemStorage circles = CvMemStorage.create();
						CvSeq seq = cvHoughCircles(thresholded,
								circles.getPointer(), CV_HOUGH_GRADIENT, 2,
								thresholded.height / 1, 20, 20, 20, 100);
						if (seq.total > 0) {
							for (int i = 0; i < seq.total; i++) {
								float xyr[] = cvGetSeqElem(seq, i)
										.getFloatArray(0, 3);
								CvPoint center = new CvPoint(
										Math.round(xyr[0]), Math.round(xyr[1]));
								variables.put(getAtPredicate().outParameters.get(0).toString(), new RawData(center.x));
								variables.put(getAtPredicate().outParameters.get(1).toString(), new RawData(center.y));
								variables.put(getAtPredicate().outParameters.get(2).toString(), new RawData(xyr[2]));
								cvCircle(grabbedImage, center.byValue(), 3,
										CvScalar.GREEN, -1, 8, 0);
								cvCircle(grabbedImage, center.byValue(),
										Math.round(xyr[2]), CvScalar.BLUE, 3,
										8, 0);
								execute(eval,variables);
							}
						}
						if (debug.getBoolean().booleanValue()) {
							frame.showImage(grabbedImage);
						}
						circles.release();
						Runtime currentRunTime = Runtime.getRuntime();
						currentRunTime.gc();
					} while (true);
				} catch (Exception e) {
					throw new RuntimeException(
							"@ballMove clause failed to evaluate");
				}
			}
		};
		mainThread.start();
	}
	@Override
	public void terminate() {
		super.terminate();
		mainThread.interrupt();
	}

}
