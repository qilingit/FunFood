# FunFood
## Projet de cours Master2 STL_INSTA TPALT

### How to create and commit to branch "test" for example :

- 1, `git checkout -b test`
- 2, `git add .`
- 3, `git commit -m "blablabla"`
- 4, `git push origin test`
- 5, `git status` (for verify everything is pushed)

### How to merge branch test to master after push :
- 1, `git checkout master`
- 2, `git pull origin master`
- 3, `git merge test`
- 4, `git push origin master`
<<<<<<< HEAD
- 5, `git status` (for verify everything is pushed)
=======
- 5, `git status (for verify everything is pushed)`

### How to pull codes from master quand on est sur  une branche "test" :

- 1, git checkout test      # gets you "on branch test"
- 2, git fetch origin        # gets you up to date with origin
- 3, git merge origin/master
>>>>>>> qilin
