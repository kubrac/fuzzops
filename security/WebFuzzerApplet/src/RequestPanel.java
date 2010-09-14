import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;



@SuppressWarnings("serial")
public class RequestPanel extends JPanel {

	final String DEFAULT_TTL = "5";
	final String DEFAULT_DEPTH = "0";
	final String DEFAULT_TIME = "60";
	
	final String ttlString = "\nThese fields configure the fuzzing process. TTL is the duration of the fuzzing process. Default is 5 minutes.\n";
	final String crawlTimeString = "\nThese fields define the crawling process and its thoroughness. A depth defines how many elements \ndeep you want to crawl (example: 5). Rather than give a depth, you can define a duration in seconds \ninstead (or both).\n";
	
	FuzzRequestBean request;
	FuzzResponseBean response;
	Socket conn;
	ObjectOutputStream oStream;
	ObjectInputStream iStream;
	
	final JTextField nameTxt;
	final JTextField emailTxt;
	final JTextField urlTxt;
	final JTextField ttlTxt;
	final JTextField depthTxt;
	final JTextField timeCrawlTxt;
	
	public RequestPanel(final FuzzerInfo fuzzerInfo){
				
		JPanel smallPanel = new JPanel();
		smallPanel.setLayout(new GridLayout(0,2,4,4));
		JPanel targPanel = new JPanel();
		targPanel.setLayout(new GridLayout(0,2,4,4));
		JPanel crawlPanel = new JPanel();
		crawlPanel.setLayout(new GridLayout(0,2,4,4));
		JPanel buttonPanel = new JPanel();
		
		JPanel innerPanel = new JPanel();
		innerPanel.setLayout(new BoxLayout(innerPanel, BoxLayout.PAGE_AXIS));
		
		JLabel nameLabel = new JLabel("*Project Request Name:");
		JLabel emailLabel = new JLabel("*Requestor's email:");
		JLabel urlLabel = new JLabel("*Base URL:");
		JLabel ttlLabel = new JLabel ("Fuzzing TTL (in minutes):");
		JLabel depthLabel = new JLabel("Crawling Depth (default: complete) :");
		JLabel timeCrawlLabel = new JLabel("Duration of Crawl (default: 60 seconds) :");
		
		nameTxt = new JTextField();
		emailTxt = new JTextField();
		urlTxt = new JTextField();
		ttlTxt = new JTextField(DEFAULT_TTL);
		depthTxt = new JTextField(DEFAULT_DEPTH);
		timeCrawlTxt = new JTextField(DEFAULT_TIME);
		
		JButton submit = new JButton("Submit");
		JButton reset = new JButton("Reset");
		
		submit.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {		
				if(!nameTxt.getText().equals("") && !emailTxt.getText().equals("") && !urlTxt.getText().equals("") ){
					request = new FuzzRequestBean(nameTxt.getText(), urlTxt.getText(), emailTxt.getText(),Integer.parseInt(ttlTxt.getText()), Integer.parseInt(depthTxt.getText()), Integer.parseInt(timeCrawlTxt.getText()));
					try {
						conn = new Socket(fuzzerInfo.getHost(), fuzzerInfo.getPortAsInt());
						oStream = new ObjectOutputStream(conn.getOutputStream());
						iStream = new ObjectInputStream(conn.getInputStream());
						if(sendRequest(request)){
							JOptionPane.showMessageDialog(null, "Success!");
							clearValues();
						} else {
							JOptionPane.showMessageDialog(null, "Failed!");
						}
					} catch (UnknownHostException e) {
						System.out.println("Error: Could no reach fuzzer! Check configurations.");
					} catch (IOException e) {
						System.out.println("Error: I/O Exception!");
					}
				} else {
					JOptionPane.showMessageDialog(null, "Error: Name, Email and URL are all required fields!");
				}
			}

			private boolean sendRequest(FuzzRequestBean request) {
				try {
					oStream.writeObject(request);
					response = (FuzzResponseBean) iStream.readObject();
					return response.isResult();
				} catch (IOException e) {
					JOptionPane.showMessageDialog(null, "Error: Could not send request!");
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
				return false;
			}
			
		});
		
		reset.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(!nameTxt.getText().equals("") || !emailTxt.getText().equals("") || !urlTxt.getText().equals("") || !ttlTxt.getText().equals(DEFAULT_TTL) || !depthTxt.getText().equals(DEFAULT_DEPTH) || !timeCrawlTxt.getText().equals(DEFAULT_TIME) ){
					int choice = JOptionPane.showConfirmDialog(null, "Are you sure you want to reset the form?");
					if(choice == JOptionPane.OK_OPTION){
						clearValues();
					}
				}
			}
			
		});
		
		JTextArea ttlHelpTxt = new JTextArea();
		ttlHelpTxt.setEditable(false);
		ttlHelpTxt.append(ttlString);
		ttlHelpTxt.setBackground(getBackground());
		ttlHelpTxt.setFont(new Font(getFont().getName(), Font.ITALIC, 13));
		
		JTextArea crawlHelpText = new JTextArea();
		crawlHelpText.setEditable(false);
		crawlHelpText.append(crawlTimeString);
		crawlHelpText.setBackground(getBackground());
		crawlHelpText.setFont(new Font(getFont().getName(), Font.ITALIC, 13));
		
		smallPanel.add(nameLabel);
		smallPanel.add(nameTxt);
		smallPanel.add(emailLabel);
		smallPanel.add(emailTxt);
		
		targPanel.add(urlLabel);
		targPanel.add(urlTxt);
		targPanel.add(ttlLabel);
		targPanel.add(ttlTxt);
		
		crawlPanel.add(depthLabel);
		crawlPanel.add(depthTxt);
		crawlPanel.add(timeCrawlLabel);
		crawlPanel.add(timeCrawlTxt);
		
		buttonPanel.add(submit);
		buttonPanel.add(reset);
		
		innerPanel.add(smallPanel);
		innerPanel.add(new JSeparator(JSeparator.HORIZONTAL),
		          BorderLayout.LINE_START);
		innerPanel.add(new JSeparator(JSeparator.HORIZONTAL),
		          BorderLayout.LINE_START);
		innerPanel.add(ttlHelpTxt);
		innerPanel.add(targPanel);
		innerPanel.add(new JSeparator(JSeparator.HORIZONTAL),
		          BorderLayout.LINE_START);
		innerPanel.add(new JSeparator(JSeparator.HORIZONTAL),
		          BorderLayout.LINE_START);
		innerPanel.add(crawlHelpText);
		innerPanel.add(crawlPanel);
		innerPanel.add(new JSeparator(JSeparator.HORIZONTAL),
		          BorderLayout.LINE_START);
		innerPanel.add(new JSeparator(JSeparator.HORIZONTAL),
		          BorderLayout.LINE_START);
		innerPanel.add(buttonPanel);
		
		add(innerPanel);

		
	}
	
	private void clearValues(){
		nameTxt.setText("");
		emailTxt.setText("");
		urlTxt.setText("");
		ttlTxt.setText(DEFAULT_TTL);
		depthTxt.setText(DEFAULT_DEPTH);
		timeCrawlTxt.setText(DEFAULT_TIME);
	}
}
