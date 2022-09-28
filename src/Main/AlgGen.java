package Main;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Arrays;

import javax.swing.JPanel;
import javax.swing.Timer;
import Graphics.DrawFunctions;

public class AlgGen extends JPanel implements ActionListener
{
	private static final long serialVersionUID = 1L;
	private Timer timer;		// Main timer of the genetic algorithm
	private int Pop = 300;
	private int run = 0, NumRuns = 4000;
	private double SelectionRate = 0.5;
	private double mutationchance = 0.9;
	private int[][] Chromosome;
	private double[] fitness;
	private int[][] selectedOnes;
	private int[] rank;
	private double[][] points;
	private double[] CumAvrfitness = null;
	private double Minfitness, Avrfitness, Maxfitness;
	private int NumTimesMutationGeneratedBestIndividual = 0;
	private boolean ShowGraphs = true, ProgramIsRunning = true;
	DrawFunctions DF;
	
	public AlgGen(int[] WinDim) 
	{		
		Initialize();
		int delay = 40;		// time to go to actionperformed
		timer = new Timer(delay, this);
		timer.start();	// Game will start checking for keyboard events and go to the method paintComponent every "timer" miliseconds
		addKeyListener(new TAdapter());
		addMouseListener(new MouseEventDemo());
		setFocusable(true);
	}
	
	public void Initialize()
	{
		String[][] InputData = Utg.ReadcsvFile("input.txt");
		points = new double[InputData.length][2];
		for (int p = 0; p <= InputData.length - 1; p += 1)
		{
			for (int c = 0; c <= 2 - 1; c += 1)
			{
				points[p][c] = Double.parseDouble(InputData[p][c]);
			}
		}
		Chromosome = Populate(Pop, points.length);
		fitness = new double[Pop];
		rank = null;
		Minfitness = 99999;
		Maxfitness = -99999;
	}
	
	public void RunAlgorithm(double[][] points, DrawFunctions DF)
	{
		if (run <= NumRuns - 1)
        {
			fitness = new double[Pop];
			for (int i = 0; i <= Pop - 1; i += 1)
			{
				fitness[i] = CalcFitness(Chromosome[i], this.points);
			}
			rank = Rank(Pop, fitness);
			selectedOnes = SelectedOnes(Pop, SelectionRate, rank, Chromosome);
			rank = Rank(Pop, fitness);
			Minfitness = Math.min(Minfitness, Utg.MinAbs(fitness));
			Avrfitness = Utg.Avr(fitness);
			Maxfitness = Math.max(Maxfitness, Utg.MaxAbs(fitness));
			CumAvrfitness = Utg.AddElem(CumAvrfitness, Avrfitness);
        }
		if (run <= NumRuns - 1)
        {
			Chromosome = NewPop(Pop, selectedOnes, SelectionRate, this.points.length);
			run += 1;
        }
	}
	
	public int[][] Populate(int Pop, int NumGenes)
	{
		int[][] Chromosomes = null;
		for (int i = 0; i <= Pop - 1; i += 1)
		{
			int[] NewChromosome = Utg.RandomChromosome(NumGenes, NumGenes - 1);
			Chromosomes = Utg.AddElem(Chromosomes, NewChromosome);
		}
		
		return Chromosomes;
	}
	
	public double CalcFitness(int[] Chromosome, double[][] points)
	{
		double fitness = Utg.dist(points[Chromosome[points.length - 1]], points[Chromosome[0]]);
		for (int p = 0; p <= points.length - 2; p += 1)
		{
			fitness += Utg.dist(points[Chromosome[p]], points[Chromosome[p + 1]]);
		}
		return fitness;
	}
		
	public int[] Rank(int Pop, double[] fitness)
	{
		int[] rank = null;
		double[] fit = Arrays.copyOf(fitness, fitness.length);
		Utg.quick_sort(fit, 0, Pop - 1);
		for (int p = 0; p <= Pop - 1; p += 1)
		{
			for (int p2 = 0; p2 <= Pop - 1; p2 += 1)
			{
				if (fitness[p2] == fit[p])
				{
					rank = Utg.AddElem(rank, p2);
					p2 = Pop - 1;
				}
			}
		}
		return rank;
	}
	
	public int[][] SelectedOnes(int Pop, double SelectionRate, int[] rank, int[][] Chromosomes)
	{
		int[][] Selected = null;
		for (int i = 0; i <= SelectionRate*Pop - 1; i += 1)
		{
			Selected = Utg.AddElem(Selected, Chromosomes[rank[i]]);
		}
		return Selected;
	}
	
	public int[][] NewPop(int Pop, int[][] parents, double SelectionRate, int NumGenes)
	{
		int[][] newPop = null;
		int NumParents = (int)(SelectionRate*Pop);
		for (int i = 0; i <= NumParents - 1; i += 1)
		{
			newPop = Utg.AddElem(newPop, parents[i]);
		}
		for (int i = NumParents; i <= Pop - 1; i += 1)
		{
			int parent1 = (int) (Math.random()*NumParents);
			int parent2 = (int) (Math.random()*NumParents);
			int[] NewChromosome = Breed(parents[parent1], parents[parent2], NumGenes);
			NewChromosome = mutation3(NewChromosome, NumGenes);
			newPop = Utg.AddElem(newPop, NewChromosome);
		}
		
		return newPop;
	}
	
	public int[] Breed(int[] parent1, int[] parent2, int NumGenes)
	{
		int[] NewGenes = null;
		for (int g = 0; g <= parent1.length - 1; g += 1)
		{
			int newgene = -1;
			if (Math.random() <= 0.5)
			{
				if (!Utg.ArrayContains(NewGenes, parent2[g]))
				{
					newgene = parent2[g];
				}
				else
				{
					newgene = parent1[g];
				}
			}
			else
			{
				if (!Utg.ArrayContains(NewGenes, parent1[g]))
				{
					newgene = parent1[g];
				}
				else
				{
					newgene = parent2[g];
				}
			}
			if (!Utg.ArrayContains(NewGenes, newgene))
			{
				NewGenes = Utg.AddElem(NewGenes, newgene);
			}
			else
			{
				NewGenes = Utg.AddElem(NewGenes, Utg.newRandomGene(NewGenes, NumGenes - 1));
			}
		}
		
		return NewGenes;
	}
	
	public int[] mutation(int[] Chromosome, int NumGenes)
	{
		int[] MutatedChromosome = Arrays.copyOf(Chromosome, Chromosome.length);
		if (Math.random() < mutationchance)
		{
			if (Math.random() < 0.5)
			{
				for (int i = 0; i <= MutatedChromosome.length - 1; i += 2)
				{
					MutatedChromosome[i] = -1;
				}
				for (int i = 0; i <= MutatedChromosome.length - 1; i += 2)
				{
					MutatedChromosome[i] = Utg.RandomNewGene(MutatedChromosome, NumGenes - 1);
				}
			}
			else
			{
				for (int i = 1; i <= MutatedChromosome.length - 1; i += 2)
				{
					MutatedChromosome[i] = -1;
				}
				for (int i = 1; i <= MutatedChromosome.length - 1; i += 2)
				{
					MutatedChromosome[i] = Utg.RandomNewGene(MutatedChromosome, NumGenes - 1);
				}
			}
		}
		return MutatedChromosome;
	}

	public int[] mutation2(int[] Chromosome, int NumGenes)
	{
		int[] MutatedChromosome = Arrays.copyOf(Chromosome, Chromosome.length);
		if (Math.random() < mutationchance)
		{
			int flippedgene = (int)(Math.random()*(NumGenes - 1));
			int originalgene = MutatedChromosome[flippedgene];
			MutatedChromosome[flippedgene] = MutatedChromosome[flippedgene + 1];
			MutatedChromosome[flippedgene + 1] = originalgene;
		}
		return MutatedChromosome;
	}

	public int[] mutation3(int[] Chromosome, int NumGenes)
	{
		int[] MutatedChromosome = Arrays.copyOf(Chromosome, Chromosome.length);
		if (Math.random() < mutationchance)
		{
			int flippedgene1 = (int)(Math.random()*NumGenes);
			int flippedgene2 = (int)(Math.random()*NumGenes);
			int originalgene = MutatedChromosome[flippedgene1];
			MutatedChromosome[flippedgene1] = MutatedChromosome[flippedgene2];
			MutatedChromosome[flippedgene2] = originalgene;
		}
		return MutatedChromosome;
	}
	
	public void PrintMutationEfficiency(int[] MutatedChromosome)
	{
		if (CalcFitness(MutatedChromosome, points) < Minfitness)
		{
			NumTimesMutationGeneratedBestIndividual += 1;
			System.out.println("A mutação gerou o melhor indivíduo " + NumTimesMutationGeneratedBestIndividual + " vezes!" + " (em " + run + " rodadas)");
		}
	}
	
	public void DrawRunResults()
	{
		DF.DrawText(new int[] {50, 50}, "Rodada: " + String.valueOf(run), "Left", 0, "Bold", 18, Color.blue);
		DF.DrawBarGraph(new int[] {50, 600}, "Adaptabilidade", new int[] {600, 200}, Color.blue, fitness, Maxfitness);
		DF.PlotPoints(new int[] {50, 350}, "Pontos", new int[] {200, 200}, Color.blue, Color.black, Utg.Transpose(points)[0], Utg.Transpose(points)[1]);
		double[] x = new double[points.length + 1], y = new double[points.length + 1];
		for (int p = 0; p <= points.length - 1; p += 1)
		{
			x[p] = points[Chromosome[rank[0]][p]][0];
			y[p] = points[Chromosome[rank[0]][p]][1];
		}
		x[points.length] = x[0];
		y[points.length] = y[0];
		x = Utg.MapLinear(Utg.MinAbs(x), Utg.MaxAbs(x), 0, 200, x);
		y = Utg.MapLinear(Utg.MinAbs(y), Utg.MaxAbs(y), 0, 200, y);
		
		for (int p = 0; p <= points.length; p += 1)
		{
			x[p] = 50 + x[p];
			y[p] = 350 - y[p];
		}
		DF.DrawDynLineGraph(new int[] {500,  400}, "Adaptabilidade média", new double[][] {CumAvrfitness}, Maxfitness, new Color[] {Color.blue});
		DF.DrawPolyLine(Utg.doubleArraytointArray(x), Utg.doubleArraytointArray(y), 2, Color.cyan);
		DF.DrawText(new int[] {50, 80}, "Melhor adaptabilidade até o momento: " + String.valueOf(Utg.Round(Minfitness, 2)), "Left", 0, "Bold", 18, Color.blue);
		DF.DrawText(new int[] {50, 100}, "Adaptabilidade média da população: " + String.valueOf(Utg.Round(Avrfitness, 2)), "Left", 0, "Bold", 18, Color.blue);
	}
	
	@Override
	public void paintComponent(Graphics g)
	{
        super.paintComponent(g);
        DF = new DrawFunctions(g);
        if (ProgramIsRunning)
        {
            RunAlgorithm(points, DF);
        }
        if (ShowGraphs)
        {
    		DrawRunResults();
        }
        Toolkit.getDefaultToolkit().sync();
        g.dispose();  
    }
	
	@Override
	public void actionPerformed(ActionEvent e) 
	{
		repaint();
		// TODO Auto-generated method stub
	}

	class TAdapter extends KeyAdapter 
	{	
	    @Override
	    public void keyPressed(KeyEvent e) 
	    {
	        int key = e.getKeyCode();
            if (key == KeyEvent.VK_PAUSE) 
            {
                if (timer.isRunning()) 
                {
                   timer.stop();
                } else 
                {
                   timer.start();
                }
            }
	    }
	
	    @Override
	    public void keyReleased(KeyEvent e) 
	    {
	    	
	    }
	}
	
	public class MouseEventDemo implements MouseListener 
	{
		@Override
		public void mouseClicked(MouseEvent evt)
		{
			
		}

		@Override
		public void mouseEntered(MouseEvent arg0)
		{
			
		}

		@Override
		public void mouseExited(MouseEvent arg0)
		{
			
		}

		@Override
		public void mousePressed(MouseEvent evt)
		{
			
		}

		@Override
		public void mouseReleased(MouseEvent e) 
		{
			
		}		
	}
}
