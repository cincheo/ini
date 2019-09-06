package ini.eval.function;

import ini.ast.Expression;
import ini.ast.Invocation;
import ini.eval.IniEval;
import ini.eval.data.Data;
import ini.parser.IniParser;
import ini.type.Type;
import ini.type.TypingConstraint;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

public class UploadFTPFunction extends IniFunction {

	@Override
	public Data eval(IniEval eval, List<Expression> params) {
		String ftpServer = eval.eval(params.get(0)).getValue();
		String user = eval.eval(params.get(1)).getValue();
		String password = eval.eval(params.get(2)).getValue();
		String fileName = eval.eval(params.get(3)).getValue();
		String fileSourceName = eval.eval(params.get(4)).getValue();
		File fileSource = new File(fileSourceName);
		try {
		uploadFTP(ftpServer, user, password, fileName, fileSource);
		}
		catch(Exception e) {
			
		}
		return null;
	}

	@Override
	public Type getType(IniParser parser, List<TypingConstraint> constraints,
			Invocation invocation) {
		return parser.types.createFunctionalType(parser.types.VOID);
	}

	public void uploadFTP(String ftpServer, String user, String password,
			String fileName, File source) throws MalformedURLException,
			IOException {
		if (ftpServer != null && fileName != null && source != null) {
			StringBuffer sb = new StringBuffer("ftp://");
			// check for authentication else assume its anonymous access.
			if (user != null && password != null) {
				sb.append(user);
				sb.append(':');
				sb.append(password);
				sb.append('@');
			}
			sb.append(ftpServer);
			sb.append('/');
			sb.append(fileName);
			/*
			 * type ==> a=ASCII mode, i=image (binary) mode, d= file directory
			 * listing
			 */
			sb.append(";type=a");

			BufferedInputStream bis = null;
			BufferedOutputStream bos = null;
			try {
				URL url = new URL(sb.toString());
				URLConnection urlc = url.openConnection();

				bos = new BufferedOutputStream(urlc.getOutputStream());
				bis = new BufferedInputStream(new FileInputStream(source));

				int i;
				// read byte by byte until end of stream
				while ((i = bis.read()) != -1) {
					bos.write(i);
				}
			} finally {
				if (bis != null)
					try {
						bis.close();
					} catch (IOException ioe) {
						ioe.printStackTrace();
					}
				if (bos != null)
					try {
						bos.close();
					} catch (IOException ioe) {
						ioe.printStackTrace();
					}
			}
		} else {
			System.out.println("Input not available.");
		}
	}

}
