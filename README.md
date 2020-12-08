#  TDT4501 - Specialization Project
### Norwegian University of Science and Technology (NTNU)  

In 1971, the B-tree data structure was invented. A couple of years after its creation, it was called ubiquitous. Now, almost 50 years later, the B-tree has kept its position as ubiquitous and has gained even more popularity due to the explosion of digitization in the past 50 years. Today, we find B-tree's typically in a wide range of databases, file systems, and operating systems. We indirectly interact with B-trees all the time. The B-tree was invented with the 1970s hardware in mind. Meanwhile, we have seen an emergence of new hardware and a massive increase in data volume. However, a small effort has been put into making the B-tree take advantage of the multi-core chips and the Non-volatile memory (NVM). With only a few modifications to the tree, it is still almost the same structure used today.


This article will take a deeper look at the current progress on optimizing the B-tree for modern hardware and the landscape of other high-performance indexes that takes advantage of today's modern hardware. It will also experiment with different architectures and algorithms to take advantage of modern hardware to improve today's B+-trees' performance. At the end of the report, a performance evaluation of the prototypes is presented.


## Purpose
At the time of the invention of the B-tree, data were stored on magnetic tapes. A storage medium made in the late 1920s with pure sequential access. Reading an entire block of data at a time was, therefore, critical. Since then, we have seen an emergence of new hardware and a massive increase in data volume. In the original B-tree paper from 1971, they expect the B-tree to theoretically be able to handle an index size of 1 500 000 items with the available hardware at the time. The index would then be able to handle two operations per second. With today's hardware and optimizations to the B-tree, the tree could process over 50 000 000 operations in a second, and the B-trees can store billions of items. However, a small effort has been put into making the B-tree take advantage of the multi-core chips, Non-volatile Memory Express (NVMe) disks, and the Non-volatile Memory (NVM). With the rise of digitization, storing large quanta of data has never been more important. Today, B-trees serves as a crucial component in systems such as filesystems and databases. Performance is immensely important for these systems, yet, the effort put into making the B-tree perform better is relatively small.

The purpose of this report is to explore the landscape of modern B-trees and present an architecture and implementation to exploit modern hardware for B+-trees, with the goal to make the proposed architecture scale on multi-core chips. 

## Research Question
How to take advantage of modern hardware, such as multi-core chips and new disk inventions, to make B-trees more efficient and scale well when using multiple CPU cores?

---

## Getting started

The benchmark test can be found [here](https://github.com/jonryf/specialization-project/tree/main/src/main/java/no/ntnu/tdt4501/benchmark/jmh/inmemory)

Implementations can be found [here](https://github.com/jonryf/specialization-project/tree/main/src/main/java/no/ntnu/tdt4501/implementation/btree)

Install the project using the gradle build automation tool;

 `gradle fatJar`
  