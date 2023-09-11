package chat;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.Socket;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Client
    extends
        JFrame
    implements
        ActionListener,
        KeyListener
{
    private static final long serialVersionUID = 1L;

    private JTextArea text;
    private JTextField txtMsg;
    private JButton btnSend;
    private JButton btnExit;
    private JLabel lblMsg;
    private JPanel centerPanel;
    private Socket socket;
    private OutputStream output;
    private Writer writer;
    private BufferedWriter bufferedWriter;
    private JTextField txtIP;
    private JTextField txtPort;
    private JTextField txtName;
    private JPanel bottomPanel;
    private JPanel messagePanel;

    public Client()
        throws IOException
    {
        renderUI();
    }

    private void renderUI()
        throws IOException
    {
        renderNewClientPanel();

        setLayout( new BorderLayout() );

        createCenterPanel();
        createBottomPanel();

        add( centerPanel, BorderLayout.CENTER );
        add( bottomPanel, BorderLayout.SOUTH );

        setTitle( txtName.getText() );
        setResizable( false );
        setSize( 800, 600 );
        setVisible( true );
        setDefaultCloseOperation( EXIT_ON_CLOSE );
    }

    private void renderNewClientPanel()
    {
        final JLabel lblMessage = new JLabel( "Dados" );
        txtIP = new JTextField( "127.0.0.1" );
        txtPort = new JTextField( "3000" );
        txtName = new JTextField( "Cliente" );
        final Object[] texts = {
            lblMessage, txtIP, txtPort, txtName
        };
        JOptionPane.showMessageDialog( null, texts );
    }

    private void createCenterPanel()
    {
        centerPanel = new JPanel( new BorderLayout() );

        createTextArea();

        final JScrollPane scroll = new JScrollPane( text );
        centerPanel.add( scroll );
    }

    private void createTextArea()
    {
        text = new JTextArea();
        text.setFont( new Font( "Serif", Font.BOLD, 20 ) );
        text.setEditable( false );
        text.setBorder( BorderFactory.createCompoundBorder(
            text.getBorder(),
            BorderFactory.createEmptyBorder( 5, 5, 5, 5 ) ) );
        text.setBorder( BorderFactory.createTitledBorder( "Chat" ) );
    }

    private void createBottomPanel()
    {
        bottomPanel = new JPanel( new BorderLayout() );

        createMessagePanel();

        bottomPanel.add( messagePanel, BorderLayout.CENTER );
    }

    private void createMessagePanel()
    {
        messagePanel = new JPanel( new FlowLayout( FlowLayout.CENTER ) );

        txtMsg = new JTextField( 20 );
        txtMsg.addKeyListener( this );

        lblMsg = new JLabel( "Mensagem" );

        btnSend = new JButton( "Enviar" );
        btnSend.setToolTipText( "Enviar Mensagem" );
        btnExit = new JButton( "Sair" );
        btnExit.setToolTipText( "Sair do Chat" );
        btnSend.addActionListener( this );
        btnExit.addActionListener( this );
        btnSend.addKeyListener( this );

        messagePanel.add( lblMsg );
        messagePanel.add( txtMsg );
        messagePanel.add( btnExit );
        messagePanel.add( btnSend );
    }

    public void connect()
        throws IOException
    {
        socket = new Socket( txtIP.getText(), Integer.parseInt( txtPort.getText() ) );
        output = socket.getOutputStream();
        writer = new OutputStreamWriter( output );
        bufferedWriter = new BufferedWriter( writer );
        bufferedWriter.write( txtName.getText() + "\r\n" );
        bufferedWriter.flush();
    }

    public void sendMessage(
        final String msg )
        throws IOException
    {
        if( msg.equalsIgnoreCase( "Sair" ) ) {
            bufferedWriter.write( "Desconectado" + "\r\n" );
            text.append( "Desconectado \r\n" );
        } else {
            bufferedWriter.write( msg + "\r\n" );
            text.append( "Eu: " + txtMsg.getText() + "\r\n" );
        }

        bufferedWriter.flush();
        txtMsg.setText( "" );
    }

    public void listen()
        throws IOException
    {
        final InputStream in = socket.getInputStream();
        final InputStreamReader inr = new InputStreamReader( in );
        final BufferedReader bfr = new BufferedReader( inr );
        String msg = "";

        while( ! msg.equalsIgnoreCase( "Sair" ) ) {
            if( bfr.ready() ) {
                msg = bfr.readLine();
                if( msg.equalsIgnoreCase( "Sair" ) ) {
                    text.append( "Servidor caiu! \r\n" );
                } else {
                    text.append( msg + "\r\n" );
                }
            }
        }
    }

    public void exit()
        throws IOException
    {
        sendMessage( "Desconectado" );
        bufferedWriter.close();
        writer.close();
        output.close();
        socket.close();
        setVisible( false );
        dispose();
        System.exit( 0 );
    }

    @Override
    public void keyPressed(
        final KeyEvent e )
    {
        if( ( e.getKeyCode() == KeyEvent.VK_ENTER ) && ! txtMsg.getText().equals( "" ) ) {
            try {
                sendMessage( txtMsg.getText() );
            } catch( final Exception e2 ) {
                e2.printStackTrace();
            }
        }
    }

    @Override
    public void actionPerformed(
        final ActionEvent e )
    {
        try {
            if( e.getActionCommand().equals( btnSend.getActionCommand() ) ) {
                sendMessage( txtMsg.getText() );
            } else if( e.getActionCommand().equals( btnExit.getActionCommand() ) ) {
                exit();
            }
        } catch( final Exception e2 ) {
            e2.printStackTrace();
        }
    }

    @Override
    public void keyTyped(
        final KeyEvent e )
    {
    }

    @Override
    public void keyReleased(
        final KeyEvent e )
    {
    }

    public static void main(
        final String[] args )
        throws IOException
    {
        final Client app = new Client();
        app.connect();
        app.listen();
    }
}
