package database;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Paint;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.apache.commons.collections15.Transformer;
import org.apache.commons.collections15.functors.ConstantTransformer;

import edu.uci.ics.jung.algorithms.layout.ISOMLayout;
import edu.uci.ics.jung.algorithms.layout.KKLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.visualization.VisualizationImageServer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.renderers.GradientVertexRenderer;
import edu.uci.ics.jung.visualization.renderers.Renderer;
import edu.uci.ics.jung.visualization.renderers.BasicVertexLabelRenderer.InsidePositioner;


public class DrawOntology extends DBconnect{
	public DrawOntology() throws InstantiationException,
			IllegalAccessException, ClassNotFoundException {
		super();
		// TODO Auto-generated constructor stub
	}
	/***/
	public static void main(String[] args) throws InstantiationException, IllegalAccessException, ClassNotFoundException, IOException, SQLException {
		DrawOntology toDraw = new DrawOntology();
		@SuppressWarnings("resource")
		BufferedReader BufferedStream = new BufferedReader(
				new FileReader("D:/dataset/Processing/Cluster/HierarchicalClusteringResult.txt"));
		String line = "";
		ArrayList<Integer> conceptList = new ArrayList<>();
		while ((line = BufferedStream.readLine()) != null) {
			if(line.contains("_concept")){
				conceptList.add(Integer.parseInt(line.split("_")[0]));
			}
		}
		for(int i:conceptList){
			toDraw.buildGraph(i);
		}
		getConn().close();
	}
	/**建立有向圖*/
	@SuppressWarnings("null")
	public void buildGraph(int no) throws IOException, SQLException{
		FileReader FileStream;
		FileStream = new FileReader("D:/dataset/Processing/TagTree/" + no + "_File/TagTreeNumberResult.txt");
		@SuppressWarnings("resource")
		BufferedReader BufferedStream = new BufferedReader(FileStream);
		String line = "";
		int i = 1;
		ArrayList<String> hierarchy_list = new ArrayList<String>();
	    DirectedSparseMultigraph<String, Number> graph = new DirectedSparseMultigraph<String, Number>();
	    String outputpath = "D:/dataset/processing/"+no+".png";
		
		while ((line = BufferedStream.readLine()) != null) {
			String topic = i + ","+ line; 
			hierarchy_list.add(topic);
			String temp = new String();
			temp=line.split(":")[0];
			graph.addVertex(temp);
			i++;
		}
	
		for(int j=0;j<hierarchy_list.size();j++){
			String parent_number = find_parent(hierarchy_list.get(j).split(":")[1]);
			String topic_id = hierarchy_list.get(j).split(",")[0];
			System.out.println(parent_number);
			if(!parent_number.isEmpty()){
				String p_topicsql = "select * from ontology where level_number = \'" + parent_number + "\' and concept_id = \'"+no+"\'";
				ResultSet p_topicrs = stmt.executeQuery(p_topicsql);
				p_topicrs.next();
				String parent_id = p_topicrs.getString("topic_id");
				graph.addEdge(new Double(Math.random()), hierarchy_list.get(Integer.parseInt(parent_id)-1).split(":")[0].split(",")[1], hierarchy_list.get(j).split(":")[0].split(",")[1], EdgeType.DIRECTED);
			}
		}
		draw(graph,outputpath);
	}
	
	/**找爸爸的level number*/
	public String find_parent(String level_number){
		String temp[]=level_number.split("\\.");
		String parent_number = "";
		for(int i=0;i<temp.length-1;i++){
			if(i!=temp.length-2)
				parent_number = parent_number+temp[i]+"."; 
			else
				parent_number = parent_number+temp[i];
		}
		System.out.println("find_parent.. "+parent_number);
		return parent_number;
	}

    VisualizationImageServer<String, Number> vv;
    
	public void draw(DirectedSparseMultigraph<String, Number> graph,String outputPath) throws IOException{
	       // create a simple graph for the demo
		  Layout<String, Number> layout = new ISOMLayout<>(graph);
			layout.setSize(new Dimension(600, 600));
        vv =  new VisualizationImageServer<String,Number>(new KKLayout<String,Number>(graph), new Dimension(600,600));

        vv.getRenderer().setVertexRenderer(
        		new GradientVertexRenderer<String,Number>(
        				Color.white, Color.red, 
        				Color.white, Color.blue,
        				vv.getPickedVertexState(),
        				false));
        vv.setBackground(Color.white);
        vv.getRenderContext().setEdgeDrawPaintTransformer(new ConstantTransformer(Color.lightGray));
        vv.getRenderContext().setArrowFillPaintTransformer(new ConstantTransformer(Color.lightGray));
        vv.getRenderContext().setArrowDrawPaintTransformer(new ConstantTransformer(Color.lightGray));
        
        vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
        vv.getRenderer().getVertexLabelRenderer().setPositioner(new InsidePositioner());
        vv.getRenderer().getVertexLabelRenderer().setPosition(Renderer.VertexLabel.Position.AUTO);

		BufferedImage image = (BufferedImage) vv.getImage(
				new Point2D.Double(layout.getSize().getWidth() / 2,
						layout.getSize().getHeight() / 2),
				new Dimension(layout.getSize()));

		// Write image to a png file
		File outputfile = new File(outputPath);
		ImageIO.write(image, "png", outputfile);
}

}
