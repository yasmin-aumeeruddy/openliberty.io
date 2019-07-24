package io.openliberty.website;

import java.io.IOException;
import java.io.InputStream;
import java.util.EnumSet;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.FilterRegistration.Dynamic;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;



public class RedirectFilter implements Filter {
  private static final Logger logger = Logger.getLogger(RedirectFilter.class.getName());
  public String from;
  public String to;
  public boolean startsWithMatch;

  public RedirectFilter(String from, String to) {
    this.to = to;
    if (from.endsWith("*")) {
      this.from = from.substring(0, from.length() - 1);
      startsWithMatch = true;
    }
  }

  @Override
  public void destroy() {
  }

  @Override
  public void init(FilterConfig cfg) {
  }

  @Override
  public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
    String newURL = null;
    String sPort = getServerPort(req);
    if (startsWithMatch) {
      // redirect pattern has a wildcard
      String uri = ((HttpServletRequest)req).getRequestURI();
      newURL = uri.replaceAll(from, to);
      if (logger.isLoggable(Level.FINER)) {
        logger.log(Level.FINE, "doFilter() variables");
        logger.log(Level.FINE, "from:", from);
        logger.log(Level.FINE, "to", to);
        logger.log(Level.FINE, "uri", uri);
        logger.log(Level.FINE, "newURL", newURL);
		  }

      if (newURL.equals(uri) || uri.equals("/docs/ref/config/") || uri.equals("/docs/ref/config/serverConfiguration.html") || uri.equals("/docs/ref/config/#serverConfiguration.html") || uri.startsWith("/docs/ref/config/#")) {
        if (logger.isLoggable(Level.FINER)) {
          logger.log(Level.FINE, "skipping redirect");
        }
        newURL = null;
      }
    } else {
      newURL = to;
    }

    if (newURL != null) {
      newURL = req.getScheme() + "://" + req.getServerName() + sPort + newURL;
      if (logger.isLoggable(Level.FINER)) {
        logger.log(Level.FINE, "Redirecting to: " + newURL);
      }
      ((HttpServletResponse)resp).sendRedirect(newURL);
    } else {
      chain.doFilter(req, resp);
    }
  }

  private String getServerPort(ServletRequest req) {
      String sPort = "";
      int serverPort = req.getServerPort();
      if ((serverPort == 80) || (serverPort == 443)) {
          // Do not add server port to the final new URL
      } else {
          sPort = ":" + serverPort;
      }
      return sPort;
  }

  public static void init(ServletContext ctx) throws IOException {
    InputStream in = ctx.getResourceAsStream("/WEB-INF/redirects.properties");
    if (in != null) {
      Properties props = new Properties();
      props.load(in);
      for (Map.Entry<?, ?> entry : props.entrySet()) {
        String key = (String) entry.getKey();
        String value = (String) entry.getValue();
        Dynamic reg = ctx.addFilter("redirect_" + entry.getKey(), new RedirectFilter(key, value));
        reg.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, toUrlPattern(key));
      }
    }
  }

  public static String toUrlPattern(String key) {
    String urlPattern = key;
    if (key.endsWith("*") && !key.endsWith("/*")) {
      int index = key.lastIndexOf('/');
      if (index == -1) {
        urlPattern = "/*";
      } else {
        urlPattern = urlPattern.substring(0, index) + "/*";
      }
    }
    return urlPattern;
  }
}
