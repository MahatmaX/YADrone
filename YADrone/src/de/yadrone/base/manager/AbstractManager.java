/*
 *
  Copyright (c) <2011>, <Shigeo Yoshida>
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
The names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF 
MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, 
SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package de.yadrone.base.manager;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

// TODO: investigate if we can refactor common parts with AbstractTCPManager
public abstract class AbstractManager implements Runnable {

	protected InetAddress inetaddr = null;
	protected DatagramSocket socket = null;
	protected boolean doStop = false;
	protected boolean connected = false;
	protected Thread thread = null;

	public AbstractManager(InetAddress inetaddr) {
		this.inetaddr = inetaddr;
	}

	public boolean connect(int port) {
		try {
			socket = new DatagramSocket(port);
			socket.setSoTimeout(3000);
		} catch (SocketException e) {
			e.printStackTrace();
			connected = false;
			return false;
		}
		connected = true;
		return true;
	}

	public boolean isConnected() {
		return connected;
	}

	public void close() {
		if (socket != null) {
			socket.close();
		}
		connected = false;
	}

	public void stop() {
		System.out.println("AbstractManager: Stopping " + getClass().getSimpleName());
		if (thread != null) {
			thread.interrupt();
			doStop = true;
			thread = null;
		}
	}

	protected void ticklePort(int port) {
		byte[] buf = { 0x01, 0x00, 0x00, 0x00 };
		DatagramPacket packet = new DatagramPacket(buf, buf.length, inetaddr, port);
		try {
			if (socket != null) {
				socket.send(packet);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void start() {
		System.out.println("AbstractManager: Starting " + getClass().getSimpleName());
		if (thread == null) {
			doStop = false;
			String name = getClass().getSimpleName();
			thread = new Thread(this, name);
			thread.start();
		} else {
			System.out.println("Already started before " + getClass().getSimpleName());
		}
	}
}
