/*
 * gpj - Global Platform for Java SmartCardIO
 *
 * Copyright (C) 2009 Wojciech Mostowski, woj@cs.ru.nl
 * Copyright (C) 2009 Francois Kooman, F.Kooman@student.science.ru.nl
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 *
 */

package pro.javacard.gp;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class GPUtils {

	public static int intValue(String s) {
		if (s.trim().toLowerCase().startsWith("0x")) {
			return Integer.parseInt(s, 16);
		}
		return Integer.parseInt(s);
	}

	public static String byteArrayToReadableString(byte[] array) {
		if (array == null) {
			return "(null)";
		}
		StringBuffer s = new StringBuffer();
		for (int i = 0; i < array.length; i++) {
			char c = (char) array[i];
			s.append(((c >= 0x20) && (c < 0x7f)) ? (c) : ("."));
		}
		return "|" + s.toString() + "|";
	}

	public static byte[] concatenate(byte[]... args) {
		int length = 0, pos = 0;
		for (byte[] arg : args) {
			length += arg.length;
		}
		byte[] result = new byte[length];
		for (byte[] arg : args) {
			System.arraycopy(arg, 0, result, pos, arg.length);
			pos += arg.length;
		}
		return result;
	}

	public static List<byte[]> splitArray(byte[] array, int blockSize) {
		List<byte[]> result = new ArrayList<byte[]>();

		int len = array.length;
		int offset = 0;
		int left = len - offset;
		while (left > 0) {
			int currentLen = 0;
			if (left >= blockSize) {
				currentLen = blockSize;
			} else {
				currentLen = left;
			}
			byte[] block = new byte[currentLen];
			System.arraycopy(array, offset, block, 0, currentLen);
			result.add(block);
			left -= currentLen;
			offset += currentLen;
		}
		return result;
	}

	public static byte[] encodeLength(int len) {
		ByteArrayOutputStream bo = new ByteArrayOutputStream();
		// XXX: can probably re-use some existing method somewhere
		if (len < 0x80) {
			bo.write((byte) len);
		} else if (len <= 0xFF) {
			bo.write((byte) 0x81);
			bo.write((byte) len);
		} else if (len <= 0xFFFF) {
			bo.write((byte) 0x82);
			bo.write((byte) ((len & 0xFF00) >> 8));
			bo.write((byte) (len & 0xFF));
		} else {
			bo.write((byte) 0x83);
			bo.write((byte) ((len & 0xFF0000) >> 16));
			bo.write((byte) ((len & 0xFF00) >> 8));
			bo.write((byte) (len & 0xFF));
		}
		return bo.toByteArray();
	}
}
