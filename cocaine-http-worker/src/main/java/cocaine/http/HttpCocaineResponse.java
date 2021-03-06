package cocaine.http;

import cocaine.http.io.HttpCocaineOutputStream;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import rx.Observer;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Locale;

/**
 * @author akirakozov
 */
public class HttpCocaineResponse implements HttpServletResponse {
    private final Multimap<String, String> headers;
    private final HttpCocaineOutputStream outputStream;

    private int status = HttpStatus.SC_200_OK;
    private String characterEncoding = null;
    private String contentType = null;
    private OutputState outState = OutputState.NONE;

    private enum OutputState {
        NONE,
        STREAM,
        WRITER
    };

    public HttpCocaineResponse(Observer<byte[]> output) {
        this.outputStream = new HttpCocaineOutputStream(output, this);
        this.headers = ArrayListMultimap.create();
    }

    @Override
    public void addCookie(Cookie cookie) {

    }

    @Override
    public boolean containsHeader(String name) {
        return headers.containsKey(name);
    }

    @Override
    public String encodeURL(String url) {
        return null;
    }

    @Override
    public String encodeRedirectURL(String url) {
        return null;
    }

    @Override
    public String encodeUrl(String url) {
        return null;
    }

    @Override
    public String encodeRedirectUrl(String url) {
        return null;
    }

    @Override
    public void sendError(int sc, String msg) throws IOException {

    }

    @Override
    public void sendError(int sc) throws IOException {

    }

    @Override
    public void sendRedirect(String location) throws IOException {

    }

    @Override
    public void setDateHeader(String name, long date) {

    }

    @Override
    public void addDateHeader(String name, long date) {

    }

    @Override
    public void setHeader(String name, String value) {
        headers.replaceValues(name, Arrays.asList(value));
    }

    @Override
    public void addHeader(String name, String value) {
        headers.put(name, value);
    }

    @Override
    public void setIntHeader(String name, int value) {
        setHeader(name, String.valueOf(value));
    }

    @Override
    public void addIntHeader(String name, int value) {
        addHeader(name, String.valueOf(value));
    }

    @Override
    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public void setStatus(int sc, String sm) {

    }

    public int getStatus() {
        return status;
    }

    @Override
    public String getCharacterEncoding() {
        return characterEncoding;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        if (outState == OutputState.WRITER) {
            throw new IllegalStateException("WRITER");
        }
        outState = OutputState.STREAM;
        return outputStream;
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        if (outState == OutputState.STREAM) {
            throw new IllegalStateException("STREAM");
        }

        if (characterEncoding == null) {
            setCharacterEncoding("utf-8");
        }
        outState = OutputState.WRITER;

        return new PrintWriter(new OutputStreamWriter(outputStream, characterEncoding));
    }

    @Override
    public void setCharacterEncoding(String charset) {
        checkNoneOutput();
        characterEncoding = charset;
        updateContentTypeHeader();
    }

    @Override
    public void setContentLength(int len) {

    }

    @Override
    public void setContentType(String type) {
        checkNoneOutput();
        contentType = type;
        updateContentTypeHeader();
    }

    @Override
    public void setBufferSize(int size) {

    }

    @Override
    public int getBufferSize() {
        return 0;
    }

    @Override
    public void flushBuffer() throws IOException {

    }

    @Override
    public void resetBuffer() {

    }

    @Override
    public boolean isCommitted() {
        return false;
    }

    @Override
    public void reset() {

    }

    @Override
    public void setLocale(Locale loc) {

    }

    @Override
    public Locale getLocale() {
        return null;
    }

    public void closeOutput() throws IOException {
        outputStream.close();
    }

    public Multimap<String, String> getHeaders() {
        return headers;
    }

    private void checkNoneOutput() {
        if (outState != OutputState.NONE) {
            throw new IllegalStateException("Set headers before getting output");
        }
    }

    private void updateContentTypeHeader() {
        headers.removeAll(HttpHeader.CONTENT_TYPE);

        if (contentType != null) {
            String val = contentType;
            val += characterEncoding == null ? "" : ";charset=" + characterEncoding;
            headers.put(HttpHeader.CONTENT_TYPE, val);
        }
    }
}
