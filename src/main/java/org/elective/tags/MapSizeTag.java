package org.elective.tags;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import java.io.IOException;
import java.util.List;
import java.util.Map;


public class MapSizeTag extends SimpleTagSupport {

    private Map<?, List<?>> map;

    public void setMap(Map<?, List<?>> map) {
        this.map = map;
    }

    @Override
    public void doTag() throws JspException, IOException {
        int sum = map.values().stream().mapToInt(List::size).sum();
        JspWriter out = getJspContext().getOut();
        out.println(sum);
    }
}
