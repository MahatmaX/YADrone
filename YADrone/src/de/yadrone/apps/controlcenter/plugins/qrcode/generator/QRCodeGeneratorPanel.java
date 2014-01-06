package de.yadrone.apps.controlcenter.plugins.qrcode.generator;


import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import de.yadrone.apps.controlcenter.ICCPlugin;
import de.yadrone.base.IARDrone;

public class QRCodeGeneratorPanel extends JPanel implements ICCPlugin
{
	private JLabel image = new JLabel();
	private JTextArea text;
	private JTextField width;
	private JTextField height;
	
	public QRCodeGeneratorPanel()
	{
		Dimension dim = new Dimension(30,20);
		width = new JTextField("300");
		width.setSize(dim);
		width.setPreferredSize(dim);
		width.setMinimumSize(dim);
		width.setMaximumSize(dim);
		
		height = new JTextField("300");
		height.setSize(dim);
		height.setPreferredSize(dim);
		height.setMinimumSize(dim);
		height.setMaximumSize(dim);
		
		text = new JTextArea("Enter your contents here");
		generateQRCode(text.getText());
		
		JButton generateButton = new JButton("Generate");
		generateButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				generateQRCode(text.getText());
			}
		});
		
		JButton printButton = new JButton("Print");
		printButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				generateQRCode(text.getText());
				print();
			}
		});
		
		setLayout(new GridBagLayout());
		add(image, new GridBagConstraints(0, 0, 6, 1, 1, 0.7, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.BOTH, new Insets(0,0,0,0), 0, 0));
		add(text, new GridBagConstraints(0, 1, 6, 1, 1, 0.3, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.BOTH, new Insets(0,0,0,0), 0, 0));
		
		add(new JLabel("Width"),  new GridBagConstraints(0, 2, 1, 1, 0.1, 0.3, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, new Insets(4,0,0,0), 0, 0));
		add(width,                  new GridBagConstraints(1, 2, 1, 1, 0.2, 0.3, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, new Insets(0,0,0,0), 0, 0));
		add(new JLabel("Height"), new GridBagConstraints(2, 2, 1, 1, 0.1, 0.3, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, new Insets(4,4,0,0), 0, 0));
		add(height,                 new GridBagConstraints(3, 2, 1, 1, 0.2, 0.3, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, new Insets(0,0,0,0), 0, 0));
		
		add(generateButton,         new GridBagConstraints(4, 2, 1, 1, 0.2, 0.3, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, new Insets(0,0,0,0), 0, 0));
		add(printButton,            new GridBagConstraints(5, 2, 1, 1, 0.2, 0.3, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, new Insets(0,0,0,0), 0, 0));
	}
	
	private void generateQRCode(String contents)
	{
		QRCodeWriter writer = new QRCodeWriter();
		BitMatrix bitMatrix = null;
		try
		{
			bitMatrix = writer.encode(contents, BarcodeFormat.QR_CODE, Integer.parseInt(width.getText()), Integer.parseInt(height.getText()));
			BufferedImage image = MatrixToImageWriter.toBufferedImage(bitMatrix);
			
			setImage(image);
		}
		catch (WriterException e)
		{
			e.printStackTrace();
		}
		
//		setSize(new Dimension(Math.min(300, Integer.parseInt(width.getText())), Math.min(400, Integer.parseInt(height.getText()))));
//		setPreferredSize(new Dimension(Math.min(300, Integer.parseInt(width.getText())), Math.min(400, Integer.parseInt(height.getText()))));
//		setMinimumSize(new Dimension(Math.min(300, Integer.parseInt(width.getText())), Math.min(400, Integer.parseInt(height.getText()))));
//		setMaximumSize(new Dimension(Math.min(300, Integer.parseInt(width.getText())), Math.min(400, Integer.parseInt(height.getText()))));
	}
	
	private void print()
	{
		PrinterJob job = PrinterJob.getPrinterJob();
		job.setPrintable(new Printable() {
			public int print(Graphics g, PageFormat pf, int page) throws PrinterException
			{
				// We have only one page, and 'page' is zero-based
				if (page > 0)
				{
					return NO_SUCH_PAGE;
				}

				// User (0,0) is typically outside the imageable area, so we must translate
				// by the X and Y values in the PageFormat to avoid clipping.
				Graphics2D g2d = (Graphics2D) g;
				g2d.translate(pf.getImageableX(), pf.getImageableY());

				// Now we perform our rendering
				g.drawImage(((ImageIcon) image.getIcon()).getImage(), 0, 0, image.getWidth(), image.getHeight(), null);
				g.drawString(text.getText(), 20, image.getHeight());
				
				// tell the caller that this page is part of the printed document
				return PAGE_EXISTS;
			}
		});

		boolean doPrint = job.printDialog();
		if (doPrint)
		{
			try
			{
				job.print();
			}
			catch (PrinterException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	private void setImage(BufferedImage image)
	{
		this.image.setIcon(new ImageIcon(image));
	}
	
	public void activate(IARDrone drone)
	{
		
	}

	public void deactivate()
	{
		
	}

	public String getTitle()
	{
		return "QRCode Generator";
	}
	
	public String getDescription()
	{
		return "Generate QR Codes (e.g. for use with the QRCode Scanner-Plugin)";
	}

	public boolean isVisual()
	{
		return true;
	}

	public Dimension getScreenSize()
	{
		return new Dimension(300, 400);
	}
	
	public Point getScreenLocation()
	{
		return new Point(400, 200);
	}

	public JPanel getPanel()
	{
		return this;
	}
}
