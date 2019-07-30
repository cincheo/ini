package ini.ext.events;

import ini.ast.AtPredicate;
import ini.ast.Rule;
import ini.eval.IniEval;
import ini.eval.at.At;
import ini.eval.data.Data;
import ini.eval.data.RawData;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class AtCopyFile extends At {
	public String sourceFileName;
	public String destFileName;
	public long copyTime;
	public boolean success = false;
	Map<String, Data> variables = new HashMap<String, Data>();
	Map<Thread, At> threadAt = new HashMap<Thread, At>();
	@Override
	public void eval(final IniEval eval) {
		final Data sfn = getInContext().get("source");
		sourceFileName = (String) sfn.getValue();
		final Data dfn = getInContext().get("destination");
		destFileName = (String) dfn.getValue();
		new Thread() {
			@Override
			public void run() {
				try {
					long beginTime = System.currentTimeMillis();
					File sourceFile = new File(sourceFileName);
					File destFile = new File(destFileName);
					//if (!destFile.exists()) {
						InputStream in = new FileInputStream(sourceFile);
						OutputStream out = new FileOutputStream(destFile);
						byte[] buffer = new byte[1024];
						int fLength;
						while ((fLength = in.read(buffer)) > 0) {
							out.write(buffer, 0, fLength);
						}
						in.close();
						out.close();
						long endTime = System.currentTimeMillis();
						copyTime = endTime - beginTime;
						success = true;
						variables.put(getAtPredicate().outParameters.get(0).toString(),
								new RawData(copyTime));
						variables.put(getAtPredicate().outParameters.get(1).toString(),
								new RawData(success));
						this.setName("Demo copy thread");
						execute(eval, variables);
						terminate();
					//}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();
	}


}
