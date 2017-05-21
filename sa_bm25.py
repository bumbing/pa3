import random
import os
import math
'''
Simulating anealing for bm25
'''

def run(a,b,c,d,e,f,g):
	text = "java -Xmx1024m -cp classes edu.stanford.cs276.Rank ./pa3-data/pa3.signal.dev bm25 ./pa1-data true %s %s %s %s %s %s %s > flowResult.txt" % (a,b,c,d,e,f,g)
	text2 = "java -Xmx1024m -cp classes edu.stanford.cs276.NdcgMain flowResult.txt pa3-data/pa3.rel.dev 1"
	os.system(text)
	score = os.popen(text2).read().rstrip()
	return score

def rundev(a,b,c,d,e,f,g):
	text = "java -Xmx1024m -cp classes edu.stanford.cs276.Rank ./pa3-data/pa3.signal.train bm25 ./pa1-data true %s %s %s %s %s %s %s > flowResult.txt" % (a,b,c,d,e,f,g)
	text2 = "java -Xmx1024m -cp classes edu.stanford.cs276.NdcgMain flowResult.txt pa3-data/pa3.rel.train 1"
	os.system(text)
	score = os.popen(text2).read().rstrip()
	return score

a = random.random()
b = random.random()
c = random.random()
d = random.random()
e = random.random()
f = random.random() * 20
g = random.random() * 10
score = run(a,b,c,d,e,f,g)
score2 = rundev(a,b,c,d,e,f,g)
T = 10000

while (T>0):
	# a_ = (random.random()-0.5) * 0.2 + a
	# b_ = (random.random()-0.5) * 0.2 + b
	# c_ = (random.random()-0.5) * 0.2 + c
	# d_ = (random.random()-0.5) * 0.2 + d
	# e_ = (random.random()-0.5) * 0.2 + e
	# f_ = (random.random()-0.5) * 200 * 0.2 + f
	# g_ = (random.random()-0.5) * 100 * 0.2 + g
	a_ = random.random()
	b_ = random.random()
	c_ = random.random()
	d_ = random.random()
	e_ = random.random()
	f_ = random.random() * 20
	g_ = random.random() * 10

	dis = run(a_,b_,c_,d_,e_,f_,g_)
	dis2 = rundev(a_,b_,c_,d_,e_,f_,g_)
	sum = float(score) + float(score2)
	newsum = float(dis) + float(dis2)
	if(newsum > sum):
		print("***************")
		print("%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t" % (a, b, c, d, e, f, g, score, score2))
		a,b,c,d,e,f,g = a_,b_,c_,d_,e_,f_,g_
		score,score2 = dis,dis2
	elif math.exp((sum - newsum) * float(T)) > random.random():
		print("***************")
		print("%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t" % (a, b, c, d, e, f, g, score, score2))
		a,b,c,d,e = a_,b_,c_,d_,e_
		score,score2 = dis,dis2
	else:
		print("***************")
		print("%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t" % (a_, b_, c_, d_, e_, f_, g_, dis, dis2))

	T = T - 1


print("Final result")
print("a : %s, b: %s, c: %s, d: %s, e: %s" % (a, b, c, d, e ))
print("score : %s" %(score))

