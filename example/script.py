import socket

s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
s.connect(("127.0.0.1", 9001))
data="哈哈哈哈123"

print(data)

dataBytes = bytes(data, "utf-8")
dataLen = len(dataBytes)

data_len = dataLen.to_bytes(4, byteorder='big')
s.sendall(data_len+dataBytes)
