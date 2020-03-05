package package_1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 计算交叉边，重叠，交叉节点个数
 * @author lidong
 *
 */

public class CrossEdge {

	public static String edgefile = "F:/暑假任务/数据文件/测试/edges.csv";
	public static String commuflie = "F:/暑假任务/数据文件/测试/comm.csv";
	public static HashMap<Integer, List<String>> commumap = new HashMap<Integer, List<String>>();//社区
	public static HashMap<String,Set<Integer>> node_commu_map = new HashMap<String,Set<Integer>>();//节点-社区
	public static HashMap<String, Set<String>> node_neighbor_map = new HashMap<String, Set<String>>();//节点-邻居
	BufferedReader br = null;
	InputStreamReader is = null;
	FileInputStream fs = null;
	
	
	/**
	 * 初始化commumap
	 * @param commufile 社区文件路径
	 */
	public void InitCommu(String commufile){
		int CNO = 0;
		try{
			fs = new FileInputStream(new File(commufile));
			is = new InputStreamReader(fs);
			br = new BufferedReader(is);
			String str = "";
			while((str = br.readLine())!=null){
				String[] commsArr = str.split("\\s+");
				List<String> list = new ArrayList<String>();
				for(int i = 0;i<commsArr.length;i++){
					list.add(commsArr[i]);
				}
				commumap.put(CNO, list);
				CNO++;
			}
			br.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	/**
	 * 初始化节点-社区map，节点-邻居map
	 * @param edgefile
	 */
	public void InitNodeItems_And_NeighMap(String edgefile){
		try{
			fs = new FileInputStream(new File(edgefile));
			is = new InputStreamReader(fs);
			br = new BufferedReader(is);
			String str = "";
			while((str = br.readLine())!=null){
				String[] nodeItem = str.split(",");
				
				if(!node_neighbor_map.containsKey(nodeItem[0])){
					Set<String> neighbor_list = new HashSet<String>();
					neighbor_list.add(nodeItem[1]);
					node_neighbor_map.put(nodeItem[0], neighbor_list);
				}else{
					Set<String> neighbor_list = node_neighbor_map.get(nodeItem[0]);
					neighbor_list.add(nodeItem[1]);
					node_neighbor_map.put(nodeItem[0], neighbor_list);
				}
				
				if(!node_neighbor_map.containsKey(nodeItem[1])){
					Set<String> neighbor_list = new HashSet<String>();
					neighbor_list.add(nodeItem[0]);
					node_neighbor_map.put(nodeItem[1], neighbor_list);
				}else{
					Set<String> neighbor_list = node_neighbor_map.get(nodeItem[1]);
					neighbor_list.add(nodeItem[0]);
					node_neighbor_map.put(nodeItem[1], neighbor_list);
				}
				
				if(!node_commu_map.containsKey(nodeItem[0])){
					Set<Integer> communities = new HashSet<Integer>();
					for(int entry: commumap.keySet()){
						List<String> commus = commumap.get(entry);
						if(commus.contains(nodeItem[0]))
							communities.add(entry);
					}
					node_commu_map.put(nodeItem[0], communities);
				}
				if(!node_commu_map.containsKey(nodeItem[1])){
					Set<Integer> communities = new HashSet<Integer>();
					for(int entry: commumap.keySet()){
						List<String> commus = commumap.get(entry);
						if(commus.contains(nodeItem[1]))
							communities.add(entry);
					}
					node_commu_map.put(nodeItem[1], communities);
				}
				
			}
			br.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
	}
	
	
	/**
	 * 计算交叉边
	 */
	public int CEdge(String edgefile){
		int cedge = 0;
		try{
			fs = new FileInputStream(new File(edgefile));
			is = new InputStreamReader(fs);
			br = new BufferedReader(is);
			String str = "";
			while((str = br.readLine())!=null){
				String[] commsArr = str.split(",");
				Set<Integer> commsarr_1_set = node_commu_map.get(commsArr[0]);
				Set<Integer> commsarr_2_set = node_commu_map.get(commsArr[1]);
				if(commsarr_1_set.size()==1&&commsarr_2_set.size()==1){
					int x_1 = 0;
					for(int x_1_1:commsarr_1_set)
						x_1 = x_1_1;
					int x_2 = 0;
					for(int x_2_2:commsarr_2_set)
						x_2 = x_2_2;
					if(x_1!=x_2){
						cedge++;
					}
				}else if(commsarr_1_set.size()==0&&commsarr_2_set.size()==0){
					
				}else{
					cedge++;
				}
			}
			br.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return cedge;
	}
	
	/**
	 * 计算重叠节点和桥接节点个数
	 * @return int
	 */
	public int sum_overlap_bridging(){
		Set<String> overlap_set = new HashSet<String>();//重叠节点
		Set<String> bridge_set = new HashSet<String>();//桥接节点
		for(String node:node_commu_map.keySet()){
			Set<Integer> node_commu_set = node_commu_map.get(node);
			if(node_commu_set.size()>1){
				overlap_set.add(node);
			}
		}
		
		
		for(String node:node_neighbor_map.keySet()){
			if(!overlap_set.contains(node)){
				Set<Integer> node_comms = node_commu_map.get(node);//节点社区set
				if(node_comms.size()==1){
					int comm = 0;
					for(int node_com_:node_comms){
						comm = node_com_;
					}
					Set<String> edges = node_neighbor_map.get(node);
					for(String node_edge:edges){
						Set<Integer> node_edge_map = node_commu_map.get(node_edge);
						if(!node_edge_map.contains(comm)){
							bridge_set.add(node);
						}
					}
				}else if(node_comms.size()==0){
					Set<Integer> comm_sum_set = new HashSet<Integer>();
					Set<String> edges = node_neighbor_map.get(node);
					if(edges.size()>1){
						for(String node_edge:edges){
							Set<Integer> node_edge_comm = node_commu_map.get(node_edge);
							comm_sum_set.addAll(node_edge_comm);
						}
						if(comm_sum_set.size()>1){
							bridge_set.add(node);
						}
					}
					
				}
				
			}
			
		}
		overlap_set.addAll(bridge_set);
		
		return overlap_set.size();
	}
	
	public static void main(String args[]){
		CrossEdge ce = new CrossEdge();
		ce.InitCommu(commuflie);
		ce.InitNodeItems_And_NeighMap(edgefile);
		int cedge = ce.CEdge(edgefile);
		int sum_bridge_overlap = ce.sum_overlap_bridging();
		System.out.println("交叉边个数："+cedge+"\n覆盖节点和桥接节点个数为："+sum_bridge_overlap);
	}
	
}
