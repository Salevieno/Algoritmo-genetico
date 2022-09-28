package Main;
import java.awt.EventQueue;
import javax.swing.JFrame;

public class MainAlgGen extends JFrame
{
	private static final long serialVersionUID = 1L;

	public MainAlgGen() 
    {
        initUI();
    }
    
    private void initUI() 
    {
    	int[] WinDim = new int[] {715, 739};	// (700 + 15, 700 + 39) to account for the frame dimensions (15, 39)
        setTitle("Star");          
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(WinDim[0], WinDim[1]);
        setLocationRelativeTo(null);    
        add(new AlgGen(WinDim));
    }
    
	public static void main(String[] args) 
	{
		EventQueue.invokeLater(() -> {MainAlgGen ex = new MainAlgGen(); ex.setVisible(true);});
	}

}
