package com.purui.myapplication.Until.QBCsuanfa;

/**
 * Created by wangruyi on 2017/7/25.
 */

public class CommPort {

    //region Field
    private String PortNum; //COM1,COM2,COM3,COM4
    private int BaudRate; //1200,2400,4800,9600
    private byte ByteSize; //8 bits
    private byte Parity; // 0-4=no,odd,even,mark,space
    private byte StopBits; // 0,1,2 = 1, 1.5, 2
    private int ReadTimeout = 10; //10
    //win32 api   ants
    public short FILE_ATTRIBUTE_NORMAL = 0x80;
    private int GENERIC_READ = 0x80000000;
    private int GENERIC_WRITE = 0x40000000;
    private int PURGE_TXABORT = 0x0001;  // Kill the pending/current writes to the comm port.
    private int PURGE_RXABORT = 0x0002;  // Kill the pending/current reads to the comm port.
    private int PURGE_TXCLEAR = 0x0004;  // Kill the transmit queue if there.
    private int PURGE_RXCLEAR = 0x0008;  // Kill the typeahead buffer if there.
    private int OPEN_EXISTING = 3;
    private int INVALID_HANDLE_VALUE = -1;
    //comm port win32 file handle
    public int hComm = -1;
    public boolean Opened = false;

    //   [StructLayout(LayoutKind.Sequential)]  //// TODO: 2017/7/25
    private class DCB {
        //taken from c struct in platform sdk
        public int DCBlength;           // sizeof(DCB)
        public int BaudRate;            // current baud rate
        public int fBinary;          // binary mode, no EOF check
        public int fParity;          // enable parity checking
        public int fOutxCtsFlow;      // CTS output flow control
        public int fOutxDsrFlow;      // DSR output flow control
        public int fDtrControl;       // DTR flow control type
        public int fDsrSensitivity;   // DSR sensitivity
        public int fTXContinueOnXoff; // XOFF continues Tx
        public int fOutX;          // XON/XOFF out flow control
        public int fInX;           // XON/XOFF in flow control
        public int fErrorChar;     // enable error replacement
        public int fNull;          // enable null stripping
        public int fRtsControl;     // RTS flow control
        public int fAbortOnError;   // abort on error
        public int fDummy2;        // reserved
        public short wReserved;          // not currently used
        public short XonLim;             // transmit XON threshold
        public short XoffLim;            // transmit XOFF threshold
        public byte ByteSize;           // number of bits/byte, 4-8
        public byte Parity;             // 0-4=no,odd,even,mark,space
        public byte StopBits;           // 0,1,2 = 1, 1.5, 2
        public char XonChar;            // Tx and Rx XON character
        public char XoffChar;           // Tx and Rx XOFF character
        public char ErrorChar;          // error replacement character
        public char EofChar;            // end of input character
        public char EvtChar;            // received event character
        public short wReserved1;         // reserved; do not use
    }

    // [StructLayout(LayoutKind.Sequential)]
    private class COMMTIMEOUTS {
        public int ReadIntervalTimeout;
        public int ReadTotalTimeoutMultiplier;
        public int ReadTotalTimeoutConstant;
        public int WriteTotalTimeoutMultiplier;
        public int WriteTotalTimeoutConstant;
    }

    //  [StructLayout(LayoutKind.Sequential)]
    private class OVERLAPPED {
        public int Internal;
        public int InternalHigh;
        public int Offset;
        public int OffsetHigh;
        public int hEvent;
    }

    // [StructLayout(LayoutKind.Sequential)]
    //private struct COMSTAT
    //{
    //    public int fCtsHold;   // Tx waiting for CTS signal
    //    public int fDsrHold;   // Tx waiting for DSR signal
    //    public int fRlsdHold;  // Tx waiting for RLSD signal
    //    public int fXoffHold;  // Tx waiting, XOFF char rec''d
    //    public int fXoffSent;  // Tx waiting, XOFF char sent
    //    public int fEof;       // EOF character sent
    //    public int fTxim;      // character waiting for Tx
    //    public int fReserved; // reserved
    //    public int cbInQue;        // bytes in input buffer
    //    public int cbOutQue;       // bytes in output buffer
    //}

    //   [StructLayout(LayoutKind.Sequential)]
    private class COMSTAT {
        /*public int fCtsHold;
        public int fDsrHold;
        public int fRlsdHold;
        public int fXoffHold;
        public int fXoffSent;
        public int fEof;
        public int fTxim;
        public int fReserved;
        public int cbInQue;
        public int cbOutQue;*/
        // Should have a reverse, i don't know why!!!!!
        public int cbOutQue;
        public int cbInQue;
        public int fReserved;
        public int fTxim;
        public int fEof;
        public int fXoffSent;
        public int fXoffHold;
        public int fRlsdHold;
        public int fDsrHold;
        public int fCtsHold;
    }


    //endregion

    //region   ructor

    public void Com(String portNum, int baudRate, int byteSize, int parity, int stopBits, int readTimeout) {
        PortNum = portNum;
        BaudRate = baudRate;
        ByteSize = (byte) byteSize;
        Parity = (byte) parity;
        StopBits = (byte) stopBits;
        ReadTimeout = readTimeout;
    }

    // [DllImport("coredll.dll")]
    private static class CreateFile {            //// TODO: 2017/7/26
        String lpFileName;                       // file name
        int dwDesiredAccess;                    // access mode
        int dwShareMode;                         // share mode
        int lpSecurityAttributes;// SD
        int dwCreationDisposition;             // how to create
        int dwFlagsAndAttributes;                 // file attributes
        int hTemplateFile;                    // handle to template file
    }

    //[DllImport("coredll.dll")]
    private static abstract class GetCommState {
        int hFile;  // handle to communications device
        String DCB;
        String lpDCB;   // device-control block
    }
}
 //   [DllImport("coredll.dll")]
    /* // 157~223
    private static  boolean BuildCommDCB(
        String lpDef,  // device-control string
   //     ref DCB lpDCB     // device-control block
);
  //  [DllImport("coredll.dll")]
    private static extern bool SetCommState(
        int hFile,  // handle to communications device
        ref DCB lpDCB    // device-control block
);
 //   [DllImport("coredll.dll")]
    private static extern bool GetCommTimeouts(
        int hFile,                  // handle to comm device
        ref COMMTIMEOUTS lpCommTimeouts  // time-out values
);
  //  [DllImport("coredll.dll")]
    private static extern bool SetCommTimeouts(
        int hFile,                  // handle to comm device
        ref COMMTIMEOUTS lpCommTimeouts  // time-out values
);
//    [DllImport("coredll.dll")]
    private static extern bool ReadFile(
        int hFile,                // handle to file
        byte[] lpBuffer,             // data buffer
        int nNumberOfBytesToRead,  // number of bytes to read
        ref int lpNumberOfBytesRead, // number of bytes read
        ref OVERLAPPED lpOverlapped    // overlapped buffer
);
  //  [DllImport("coredll.dll")]
    private static extern bool WriteFile(
        int hFile,                    // handle to file
        byte[] lpBuffer,                // data buffer
        int nNumberOfBytesToWrite,     // number of bytes to write
        ref int lpNumberOfBytesWritten,  // number of bytes written
        ref OVERLAPPED lpOverlapped        // overlapped buffer
);
 //   [DllImport("coredll.dll")]
    private static extern bool CloseHandle(
        int hObject   // handle to object
);

//    [DllImport("coredll.dll")]
    private static extern int GetFileSize(
        int hFile,
        ref int lpFileSizeHigh
);

  //  [DllImport("coredll.dll")]
    private static extern bool ClearCommError(
        int hFile,   // 串口句柄
        ref int lpErrors,   // 指向接收错误码的变量
        ref COMSTAT lpStat    // 指向通讯状态缓冲区
);

  //  [DllImport("coredll.dll")]
    private static extern bool  SetupComm(
        int hFile,   // 串口句柄
        int dwInQueue,    // 输入缓冲区的大小（字节数）
        int dwOutQueue    // 输出缓冲区的大小（字节数）
);

  //  [DllImport("coredll.dll")]
    private static extern bool PurgeComm(
        int hFile,     // handle to file
        int dwFlags
);
*/
    //endregion
    //打开串口
    /*
    public  boolean Open ()
    {
        Opened = false;

        DCB dcbCommPort = new DCB();
        COMMTIMEOUTS ctoCommPort = new COMMTIMEOUTS();
//// TODO: 2017/7/26
        // OPEN THE COMM PORT.
        hComm = CreateFile(PortNum, GENERIC_READ | GENERIC_WRITE, 0, 0, OPEN_EXISTING, 0, 0);
        // IF THE PORT CANNOT BE OPENED, BAIL OUT.
        if (hComm == INVALID_HANDLE_VALUE)
        {
            //Close();
            return false;
        }
        SetupComm(hComm, 1024, 1024);
        // SET THE COMM TIMEOUTS.

        GetCommTimeouts(hComm, ref ctoCommPort);
        ctoCommPort.ReadTotalTimeout  ant = ReadTimeout;
        ctoCommPort.ReadTotalTimeoutMultiplier = 0;
        ctoCommPort.WriteTotalTimeoutMultiplier = 0;
        ctoCommPort.WriteTotalTimeout  ant = 0;

        SetCommTimeouts(hComm, ref ctoCommPort);

        dcbCommPort.DCBlength = Marshal.SizeOf(dcbCommPort);
        GetCommState(hComm, ref dcbCommPort);
        dcbCommPort.BaudRate = BaudRate;
        dcbCommPort.Parity = Parity;
        dcbCommPort.ByteSize = ByteSize;
        dcbCommPort.StopBits = StopBits;
        dcbCommPort.fAbortOnError = 0;
        SetCommState(hComm, ref dcbCommPort);
        Opened = true;
        return true;
    }
    //关闭串口
    public void Close()
    {
        CloseHandle(hComm);
        Opened = false;
    }

     /*   public byte[] Read(int NumBytes)
        {
            byte[] BufBytes;
            byte[] OutBytes;
            BufBytes = new byte[NumBytes];
            if (hComm != INVALID_HANDLE_VALUE)
            {
                OVERLAPPED ovlCommPort = new OVERLAPPED();
                int BytesRead = 0;
                bool b = false;
                b = ReadFile(hComm, BufBytes, NumBytes, ref BytesRead, ref ovlCommPort);
                OutBytes = new byte[BytesRead];
                Array.Copy(BufBytes, OutBytes, BytesRead);
            }
            else
            {
                throw (new ApplicationException("Comm Port Not Open"));
            }
            return OutBytes;
        }*/


/*

    public int Write(byte[] WriteBytes)
    {
        int BytesWritten = 0;
        OVERLAPPED ovlCommPort = new OVERLAPPED();
        COMSTAT ComStat = new COMSTAT();

        int dwErrorFlags = 0;
        ClearCommError(hComm, ref dwErrorFlags, ref ComStat);
        if (dwErrorFlags != 0)
            PurgeComm(hComm, PURGE_TXABORT | PURGE_TXCLEAR);
        bool b = WriteFile(hComm, WriteBytes, WriteBytes.Length, ref BytesWritten, ref ovlCommPort);
        return BytesWritten;
    }

    //写入string
    public int Write(string WriteBytes)
    {
        int BytesWritten = 0;
        byte[] bpara = System.Text.Encoding.Default.GetBytes(WriteBytes);
        OVERLAPPED ovlCommPort = new OVERLAPPED();
        COMSTAT ComStat = new COMSTAT();
        int dwErrorFlags = 0;
        ClearCommError(hComm, ref dwErrorFlags, ref ComStat);
        if (dwErrorFlags != 0)
            PurgeComm(hComm, PURGE_TXABORT | PURGE_TXCLEAR);
        bool b = WriteFile(hComm, bpara, bpara.Length, ref BytesWritten, ref ovlCommPort);
        return BytesWritten;
    }
    //读取串口返回数据长度
    public int ByteToRead()
    {
        COMSTAT ComStat = new COMSTAT();

        int dwErrorFlags = 0;
        ClearCommError(hComm, ref dwErrorFlags, ref ComStat);
        if (dwErrorFlags != 0)
            PurgeComm(hComm, PURGE_RXCLEAR | PURGE_RXABORT);

        return ComStat.cbInQue;
    }
    //读取数据
    public boolean ReadPort(int NumBytes,  byte[] commRead)
    {
        COMSTAT ComStat = new COMSTAT();
        int BytesRead = 0;
        OVERLAPPED ovlCommPort = new OVERLAPPED();
        return ReadFile(hComm, commRead, NumBytes, ref BytesRead, ref ovlCommPort);

    }

        }
}
}
}*/