package dm.view;

import com.mxgraph.view.mxGraph;
import org.htmlcleaner.*;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User:
 * Date: 10.12.12
 * Time: 11:53
 * To change this template use File | Settings | File Templates.
 */
public class Application {
    public static void main(String[] args) {
        System.out.println("Hi");
        HtmlCleaner cleaner = new HtmlCleaner();

        String host = "http://www.linux.org.ru";
        final String threadHref = "/forum/talks/8571380";
        TagNode thread = null;
        try {
            thread = cleaner.clean(new URL(host + threadHref));
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        List<String> hrefs2pages = new ArrayList<String>() {{
            add(threadHref);
        }};

        if (thread != null) {
            List<TagNode> navs = thread.getElementListByAttValue("class", "nav", true, true);
            if (navs.size() > 1) {
                TagNode nav = navs.get(1);
                List<TagNode> pages = nav.getElementListByAttValue("class", "page-number", true, true);
                if(pages != null && pages.size() != 0) {
                    pages.remove(0);
                    pages.remove(pages.size()-1);
                }
                System.out.println("test " + nav.getText());
                for(TagNode page : pages) {
                    System.out.println("test");
                    String href = page.getAttributeByName("href");
                    if(href != null) {
                        hrefs2pages.add(href);
                        System.out.println(href);
                    }
                }
            }
        }

        Pattern pAnswerId = Pattern.compile("cid=(\\d+)");
        Pattern pCommentId = Pattern.compile("\\d+");

        HashMap<Integer, Object> id2vertex = new HashMap<Integer, Object>();
        mxGraph graph = new mxGraph();
        Object parent = graph.getDefaultParent();

        id2vertex.put(0, graph.insertVertex(parent, null, "root", 20, 20, 80, 30));

        for(String threadHrefPage : hrefs2pages) {
            System.out.println("test " + host + threadHrefPage);
            // by page
            TagNode node = null;
            try {
                node = cleaner.clean(new URL(host + threadHrefPage));
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            assert node != null;

            List<TagNode> articles = node.getElementListByName("article", true);
            Iterator<TagNode> artIterator = articles.iterator();

            graph.getModel().beginUpdate();

            while(artIterator.hasNext()) {
                TagNode article = artIterator.next();

                TagNode aHref = article.findElementByAttValue("class", "title", true, false)
                        .findElementByName("a", false);

                Matcher answerIdIter = pAnswerId.matcher("cid=0");
                if (aHref != null) {
                    String href = aHref.getAttributeByName("href");
                    System.out.println(href);
                    answerIdIter = pAnswerId.matcher(href);
                }
                Matcher commentIdIter = pCommentId.matcher(article.getAttributeByName("id"));
                int commentId = 0;
                while (commentIdIter.find()) {
                    commentId = Integer.parseInt(commentIdIter.group());
                }
                System.out.println("-->" + commentId);

                while(answerIdIter.find()) {
                    int cid = Integer.parseInt(answerIdIter.group(1));
                    id2vertex.put(
                            commentId,
                            graph.insertVertex(
                                    parent, null,
                                    article.findElementByAttValue("class", "sign", true, false).getText()
                                        + " (" + commentId + ")"
                                    , 240, 150, 80, 30
                            )
                    );

                    graph.insertEdge(id2vertex.get(0), null, "", id2vertex.get(commentId), id2vertex.get(cid));
                }
            }

            graph.getModel().endUpdate();
        }
        MainFrame mf = new MainFrame(graph);

    }

}
