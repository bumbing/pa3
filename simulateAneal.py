import random
import os
import math

def run(a,b,c,d):
	text = "java -Xmx1024m -cp classes edu.stanford.cs276.Rank ./pa3-data/pa3.signal.train cosine ./pa1-data true %s %s %s %s > flowResult.txt" % (a,b,c,d)
	text2 = "java -Xmx1024m -cp classes edu.stanford.cs276.NdcgMain flowResult.txt pa3-data/pa3.rel.train 1"
	os.system(text)
	score = os.popen(text2).read().rstrip()
	return score


a = random.random() * 500
b = random.random() * 500
c = random.random() * 500
d = random.random() * 500
score = run(a,b,c,d)
T = 10000

while (T>0):
	a_ = random.random() * 500
	b_ = random.random() * 500
	c_ = random.random() * 500
	d_ = random.random() * 500
	dis = run(a_,b_,c_,d_)
	if(dis>score):
		a,b,c,d = a_,b_,c_,d_
		score = dis
	if math.exp(float(dis)/float(T)) > random.random():
		a,b,c,d = a_,b_,c_,d_
		score = dis
	T = T - 1
	print("***************")
	print("a : %s, b: %s, c: %s, d: %s" % (a, b, c, d))
	print("score : %s" %(score))

print("Final result")
print("a : %s, b: %s, c: %s, d: %s" % (a, b, c, d))
print("score : %s" %(score))

