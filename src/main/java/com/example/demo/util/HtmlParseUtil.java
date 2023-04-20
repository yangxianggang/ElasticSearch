package com.example.demo.util;

import com.example.demo.pojo.Content;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class HtmlParseUtil {
    public static void main(String[] args) throws IOException {




    }

    public static List<Content>getContent(String key) throws IOException{

        //https://re.jd.com/search?keyword=java&enc=utf-8
        //获取请求
        String url="https://search.jd.com/Search?keyword="+key;


        //解析网页
        Document document = Jsoup.parse(new URL(url), 30000);
        //所有你在js中可以使用的方法在这里都能用
        Element j_goodsList = document.getElementById("J_goodsList");
        // Element j_goodsList = document.getElementById("sear_shop_list");
        //获取所有的i元素
        // System.out.println(j_goodsList.html());
        Elements elements = j_goodsList.getElementsByTag("li");
        List<Content>contents=new ArrayList<>();
        for (Element el : elements) {
            String img = el.getElementsByTag("img").eq(0).attr("data-lazy-img");
            String price = el.getElementsByClass("p-price").eq(0).text();
            String title = el.getElementsByClass("p-name").eq(0).text();
            System.out.println("--------------------------------------------------");
            System.out.println(img);
            System.out.println(price);
            System.out.println(title);
            contents.add(new Content(title,img,price));
        }
        return contents;

    }
}
