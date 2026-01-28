# Git Commands Reference

This file contains useful Git commands for managing your opensearch-demo repository.

## Repository Information

- **Repository URL**: https://github.com/gargvk81/opensearch-demo
- **Clone URL**: `git clone https://github.com/gargvk81/opensearch-demo.git`

## Common Git Workflows

### Check Status
```bash
git status
```

### View Commit History
```bash
git log --oneline
git log --graph --oneline --all
```

### Making Changes

1. **Check what changed**
```bash
git status
git diff
```

2. **Stage changes**
```bash
# Stage specific files
git add pom.xml
git add src/main/java/com/example/opensearch/model/Product.java

# Stage all changes
git add .

# Stage all Java files
git add *.java
```

3. **Commit changes**
```bash
git commit -m "Your commit message here"

# Commit with detailed message
git commit -m "Add new feature" -m "Detailed description of changes"
```

4. **Push to GitHub**
```bash
git push origin main
```

### Creating New Features

1. **Create a new branch**
```bash
git checkout -b feature/new-search-functionality
```

2. **Make changes and commit**
```bash
git add .
git commit -m "Add new search functionality"
```

3. **Push branch to GitHub**
```bash
git push origin feature/new-search-functionality
```

4. **Create Pull Request on GitHub**
```bash
gh pr create --title "Add new search functionality" --body "Description of changes"
```

5. **Merge and delete branch**
```bash
# After PR is merged, switch back to main
git checkout main
git pull origin main

# Delete local branch
git branch -d feature/new-search-functionality
```

### Syncing with Remote

```bash
# Fetch changes from GitHub
git fetch origin

# Pull changes from GitHub
git pull origin main

# Pull with rebase (cleaner history)
git pull --rebase origin main
```

### Viewing Changes

```bash
# See what changed in last commit
git show

# See changes in specific commit
git show a5c4a16

# See changes in specific file
git diff pom.xml

# See file at specific commit
git show main:pom.xml
```

### Undoing Changes

```bash
# Discard changes in working directory
git checkout -- filename.java

# Unstage a file (keep changes)
git reset HEAD filename.java

# Undo last commit (keep changes)
git reset --soft HEAD~1

# Undo last commit (discard changes) - USE WITH CAUTION
git reset --hard HEAD~1

# Revert a commit (creates new commit)
git revert abc123
```

### Branch Management

```bash
# List all branches
git branch
git branch -a  # including remote branches

# Switch to existing branch
git checkout main
git checkout feature-branch

# Create and switch to new branch
git checkout -b new-feature

# Delete local branch
git branch -d branch-name

# Delete remote branch
git push origin --delete branch-name

# Rename current branch
git branch -m new-branch-name
```

### Tagging Releases

```bash
# Create annotated tag
git tag -a v1.0.0 -m "Version 1.0.0 - Initial release"

# Push tag to GitHub
git push origin v1.0.0

# Push all tags
git push origin --tags

# List all tags
git tag

# Delete tag
git tag -d v1.0.0
git push origin --delete v1.0.0
```

### Stashing Changes

```bash
# Stash current changes
git stash

# Stash with message
git stash save "Work in progress on search feature"

# List stashes
git stash list

# Apply most recent stash
git stash apply

# Apply and remove stash
git stash pop

# Apply specific stash
git stash apply stash@{0}

# Clear all stashes
git stash clear
```

### Working with Remotes

```bash
# View remotes
git remote -v

# Add remote
git remote add upstream https://github.com/original/repo.git

# Change remote URL
git remote set-url origin https://github.com/gargvk81/opensearch-demo.git

# Remove remote
git remote remove upstream
```

## GitHub CLI (gh) Commands

### Repository Management

```bash
# View repository in browser
gh repo view --web

# Clone repository
gh repo clone gargvk81/opensearch-demo

# Create new repository
gh repo create new-repo --public

# Archive repository
gh repo archive gargvk81/opensearch-demo
```

### Pull Requests

```bash
# List PRs
gh pr list

# Create PR
gh pr create --title "Feature" --body "Description"

# View PR in browser
gh pr view 123 --web

# Checkout PR locally
gh pr checkout 123

# Merge PR
gh pr merge 123

# Review PR
gh pr review 123 --approve
gh pr review 123 --comment -b "Looks good!"
```

### Issues

```bash
# List issues
gh issue list

# Create issue
gh issue create --title "Bug" --body "Description"

# View issue
gh issue view 123

# Close issue
gh issue close 123
```

## Typical Development Workflow

### Daily Work
```bash
# 1. Start your day - sync with remote
git checkout main
git pull origin main

# 2. Create feature branch
git checkout -b feature/add-autocomplete

# 3. Make changes and commit frequently
git add .
git commit -m "Add autocomplete endpoint"

# 4. Push to GitHub
git push origin feature/add-autocomplete

# 5. Create PR when ready
gh pr create --title "Add autocomplete functionality" --web
```

### Keeping Branch Updated
```bash
# Switch to main and update
git checkout main
git pull origin main

# Switch back to feature branch
git checkout feature/add-autocomplete

# Rebase on latest main
git rebase main

# Force push if already pushed (USE WITH CAUTION)
git push --force-with-lease origin feature/add-autocomplete
```

## Useful Aliases

Add these to your `~/.gitconfig`:

```ini
[alias]
    st = status
    co = checkout
    br = branch
    ci = commit
    unstage = reset HEAD --
    last = log -1 HEAD
    visual = log --graph --oneline --all
    amend = commit --amend --no-edit
    undo = reset --soft HEAD~1
```

Then use them like:
```bash
git st          # instead of git status
git co main     # instead of git checkout main
git br          # instead of git branch
```

## Best Practices

1. **Commit Often**: Make small, focused commits
2. **Write Good Commit Messages**: Clear, descriptive messages
3. **Use Branches**: One branch per feature/fix
4. **Pull Before Push**: Always sync before pushing
5. **Review Before Commit**: Use `git diff` to review changes
6. **Don't Commit Secrets**: Never commit passwords, API keys, etc.
7. **Use .gitignore**: Keep generated files out of version control

## Emergency Commands

### Accidentally committed to main instead of feature branch
```bash
git reset --soft HEAD~1  # Undo commit, keep changes
git checkout -b feature/my-feature  # Create feature branch
git add .
git commit -m "Your commit message"
```

### Accidentally committed sensitive data
```bash
# Remove from last commit
git rm --cached sensitive-file.txt
git commit --amend --no-edit
git push --force-with-lease origin main

# Remove from history (complex, use with caution)
git filter-branch --index-filter 'git rm --cached --ignore-unmatch sensitive-file.txt' HEAD
```

### Merge conflicts
```bash
# When pulling causes conflict
git pull origin main
# Fix conflicts in files
git add .
git commit -m "Resolve merge conflicts"
```

## Resources

- Git Documentation: https://git-scm.com/doc
- GitHub Docs: https://docs.github.com
- GitHub CLI: https://cli.github.com/manual/

---

**Repository**: https://github.com/gargvk81/opensearch-demo
