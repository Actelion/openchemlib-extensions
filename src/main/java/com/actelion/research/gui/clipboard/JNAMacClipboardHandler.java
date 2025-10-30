/*
 * Copyright (c) 2025,
 * Christian Rufener
 * Alipheron AG
 * www.alipheron.com
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 3. Neither the name of the the copyright holder nor the
 *    names of its contributors may be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */
package com.actelion.research.gui.clipboard;

import com.actelion.research.gui.clipboard.foundation.NSData;
import com.actelion.research.gui.clipboard.foundation.NSPasteboard;
import com.actelion.research.gui.clipboard.foundation.NSString;
import com.sun.jna.Pointer;

import java.util.HashMap;
import java.util.Map;

public class JNAMacClipboardHandler {
    private static boolean isInitOK = true; // dummy to match NativeClipboardAccessor
    private static NSPasteboard pasteboard = NSPasteboard.generalPasteboard();
    private static int fmId = 1;

    public static boolean isInitOK() {
        return isInitOK;
    }

    public static byte[] getClipboardData(String format) {
        byte[] data = null;
        NSData dataForType = pasteboard.getDataForType(new NSString(format));
        if (dataForType != null) {
            int size = dataForType.length();
            data = new Pointer(dataForType.getData().longValue()).getByteArray(0, size);
        }
        return data;
    }

    public static boolean setClipBoardData(String format, byte[] buffer) {
        return setClipBoardData(format, buffer, true);
    }

    public static boolean setClipBoardData(String format, byte[] buffer, boolean emptyClipboard) {
        NSData data = NSData.initWithBytes(buffer);
        if (emptyClipboard)
            pasteboard.clearContents();
        return pasteboard.setData(data, new NSString(format));
    }

}
