package chat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

public class Server
    extends
        Thread
{
    private static ArrayList<BufferedWriter> clients;
    private static ServerSocket server;
    private String name;
    private final Socket port;
    private InputStream input;
    private InputStreamReader inputReader;
    private BufferedReader bufferedReader;

    public Server(
        final Socket port )
    {
        this.port = port;

        try {
            input = port.getInputStream();
            inputReader = new InputStreamReader( input );
            bufferedReader = new BufferedReader( inputReader );
        } catch( final IOException e ) {
            e.printStackTrace();
        }
    }

    @Override
    public void run()
    {
        try {
            String msg;
            final OutputStream output = port.getOutputStream();
            final Writer writer = new OutputStreamWriter( output );
            final BufferedWriter bufferedWriter = new BufferedWriter( writer );
            clients.add( bufferedWriter );
            name = msg = bufferedReader.readLine();

            while( ( msg != null ) && ! msg.equals( "Desconectado" ) ) {
                msg = bufferedReader.readLine();
                sendToAll( bufferedWriter, msg );
                System.out.println( msg );
            }

            System.out.println( "alguem saiu" );
            clients.remove( bufferedWriter );
        } catch( final Exception e ) {
            e.printStackTrace();
        }
    }

    private void sendToAll(
        final BufferedWriter bufferedWriter,
        final String msg )
        throws IOException
    {
        BufferedWriter bufferedOutput;

        for( final BufferedWriter bw : clients ) {
            bufferedOutput = bw;
            if( ( bufferedWriter != bufferedOutput ) && ( msg != null ) ) {
                bw.write( name + ": " + msg + "\r\n" );
                bw.flush();
            }
        }
    }

    public static void main(
        final String[] args )
    {
        try {
            final JLabel lblMessage = new JLabel( "Porta do Servidor:" );
            final JTextField txtPort = new JTextField( "3000" );
            final Object[] texts = {
                lblMessage, txtPort
            };
            JOptionPane.showMessageDialog( null, texts );
            server = new ServerSocket( Integer.parseInt( txtPort.getText() ) );
            clients = new ArrayList<>();

            while( true ) {
                System.out.println( "Aguardando conexao..." );

                final Socket port = server.accept();
                System.out.println( "Cliente conectado!" );

                final Thread t = new Server( port );
                t.start();
            }
        } catch( final Exception e ) {
            e.printStackTrace();
        }
    }
}
