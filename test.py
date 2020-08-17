import http.server
import socketserver
#import BaseHTTPServer

PORT = 8081

class MyHandler(http.server.BaseHTTPRequestHandler):
    def do_GET(s):
        s.send_response(200)
        s.send_header("Content-type", "application/json")
        s.end_headers()
        s.wfile.write('{"x": 10}'.encode())

#Handler = http.server.SimpleHTTPRequestHandler

with socketserver.TCPServer(("", PORT), MyHandler) as httpd:
    print("serving at port", PORT)
    httpd.serve_forever()
