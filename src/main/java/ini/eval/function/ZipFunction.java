package ini.eval.function;

import ini.ast.Expression;
import ini.ast.Invocation;
import ini.eval.IniEval;
import ini.eval.data.Data;
import ini.parser.IniParser;
import ini.type.Type;
import ini.type.TypingConstraint;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipFunction extends IniFunction {

	@Override
	public Data eval(IniEval eval, List<Expression> params) {
		String fileName = eval.eval(params.get(0)).getValue();
		String zipFileName = eval.eval(params.get(1)).getValue();
		File zipFile = new File(zipFileName);
		zipFile.delete();
		doZip(fileName, zipFileName);
		return null;

	}

	@Override
	public Type getType(IniParser parser, List<TypingConstraint> constraints,
			Invocation invocation) {
		return parser.types.createFunctionalType(parser.types.VOID);
	}

	public void doZip(String fileName, String zipFileName) {
		try {
			byte[] buf = new byte[1024];
			FileInputStream fis = new FileInputStream(fileName);
			fis.read(buf, 0, buf.length);

			CRC32 crc = new CRC32();
			ZipOutputStream s = new ZipOutputStream(
					(OutputStream) new FileOutputStream(zipFileName));
			s.setLevel(6);
			ZipEntry entry = new ZipEntry(fileName);
			entry.setSize((long) buf.length);
			crc.reset();
			crc.update(buf);
			entry.setCrc(crc.getValue());
			s.putNextEntry(entry);
			s.write(buf, 0, buf.length);
			s.finish();
			s.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
