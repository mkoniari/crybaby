#!/usr/bin/python

f = open('classids.txt')
asdf = [x for x in f.readlines() if x.find('From page') < 0]
f.close()
counts = {}
num = 0
for line in asdf:
	left = line.find('[')
	line = line[left + 1 : -1].split(', ')
	#for thing in line:
	#	counts[thing] = counts.setdefault(thing, 0) + 1
#print 'ID or class,Key'
#for thing in counts:
#	print '%s,%d' % (thing,counts[thing])
