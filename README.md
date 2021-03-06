# Paillier-voting
Secure voting system using Paillier
Crypto I Project by Daryian Rhysing, Alex Giris, and James Ziron

This repository is a portion of the project by Alex Giris only, although one of the other two located the JPaillier library.

# How to build
Before running, please change the voters.txt and the candidates.txt to the appropriate
lists of voters and candidates, one per line, do not include any additional line breaks at the
beginning or the end. Do not separate lines with commas or any other punctuation unless such marks
are actually part of the voter's name.

Compile all enclosed java files and run the main class. The expertly crafted GUI will
guide you through the voting process in a completely unbiased manner. 

At the start, the Bulletin Board is initialized with a vote of zero for all voter/candidate combinations.
This is to protect the confidentiality of voters who wish to abstain from the vote.

# About
This project was written as a project for the Cryptography I class at RPI. Since submitting it, I fixed
a problem with the Zero Knowledge Proof, changed some of the error messages, and removed a few unnecessary 
print statements.

The voting system is split into three parts: The Election Board, the Bulletin Board, and the Counting Authority.
-The Election Board registers all voters and signs ballots with RSA to indicate that they are authentic.
-The Bulletin Board stores all the votes as a 1 or a 0 for each voter/candidate combination. One of the features
of Paillier is that there are many different ways to encrypt any given input, so a hacker is unlikely to be able
to figure out any votes from the encrypted board.
-The Counting Authority adds up the votes without decrypting any individual votes and announces the result.

Known vulnerabilities:
The Bulletin Board may accept votes that contain values other than zero or one. This allows voters to effectively
"vote" for the same candidate 1000 times, or vote for multiple candidates. The idea of this project was to avoid
decrypting individual votes, so I believe preventing this is beyond the scope of the project.

There is no voter authentication other than entering the voter's name. Any further measures are beyond the scope
of this project.





No animals were harmed in the making of this program.
