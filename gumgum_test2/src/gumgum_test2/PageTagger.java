package gumgum_test2;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.net.*;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;


public class PageTagger {
	
	private MaxentTagger tagger = null;
	
	private final int PARTMAX = 4096;
	private final String TAGPATH = "taggers/english-left3words-distsim.tagger";
	
	
	public PageTagger(){

		tagger = new  MaxentTagger(TAGPATH);
	}
	
	
	public List<String> partions(String in){
		
		List<String> res = new ArrayList<String>(); 
		
		StringBuffer sb = new StringBuffer();
		int len = in.length();
		int i = 0 ;
		while (i< len){
			if ( i+PARTMAX-1 >= len){
				String part = in.substring(i, len);
				res.add(part);
				i = len;
			} else{
				// sentence end
				int endIndex = in.lastIndexOf('.', i+PARTMAX-1);
				
				// word seperation
				if (endIndex == -1)
					endIndex = in.lastIndexOf(' ', i+PARTMAX-1);
				
				if (endIndex == -1)
					endIndex = PARTMAX;
				else
					endIndex = endIndex+1; // absurb bounddary to the previous character
					
					
				String part = in.substring(i, endIndex);
				res.add(part);
				i = endIndex;
			}
		}
		
		
		return res;
	}
	
	public String tagText(String in){
		
		
		//replace Unicode Charater
		// \uFFFD, something unrecognized 
		// U+EA02, 
		String inPuri = in.replaceAll("\uFFFD","");
		
		List<String> res = partions(inPuri);
		StringBuffer sb = new StringBuffer(); 
		for (String s : res){
			sb.append(tagger.tagString(s));
		}
		
		return sb.toString();
	}
	
	
	public String getText(String strUrl){
		StringBuffer sb = new StringBuffer();
		try{
			BufferedReader br = null;
			try{
				URL url = new URL(strUrl);
				br = new BufferedReader(new InputStreamReader(url.openStream()));
				String s ="";
				while((s=br.readLine())!=null){ 
					sb.append(s+"\r\n");
					//sb.append(s+"\n");
					//sb.append(s);
					//System.out.println(sb.length());
				}
			
			}finally{
				br.close();
			}
		}catch(Exception e){
				return "error open url" + strUrl;
		}
		

		System.out.println(sb.length());
		
		String cont = sb.toString();
		Document doc = Jsoup.parseBodyFragment(cont);
		
		String text = doc.body().text(); 
		
		// cannot print all because println size limitation
		//System.out.println(text);
		System.out.println(text.length());
		
		//String text = sb.toString();
		return text;
		
	}
	
	
	public static void main(String [] args){
		
		
        URL url = null;
        if (args.length < 1) {
            System.err.println("No input argument provided");
            System.exit(1);
        }
		
		PageTagger pagetagger= new PageTagger(); 	
		
		String pretagtext = pagetagger.getText(args[0]);
		String taggedtext = pagetagger.tagText(pretagtext);
		
		
		// result output
		System.out.println(taggedtext);
		// file output verify
		
		/*try{
        	PrintWriter out = new PrintWriter("res.txt");
        	try{
        		out.println(taggedtext);
        	}finally{
        			out.close();
        	}
        }catch (IOException e){
        	e.printStackTrace();
        }*/
		
		
		
	}
	
}
