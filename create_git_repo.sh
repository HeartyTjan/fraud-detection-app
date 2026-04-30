#!/bin/bash
# ============================================
# 🚀 Automation Script (v4)
# ============================================



LOGFILE="repo_creation.log"
STATE_FILE=".script_state"

error_handler() {
    local exit_code=$?
    local last_command="${BASH_COMMAND}"
    local line_no="${BASH_LINENO[0]}"
    echo ""
    echo "❌ ERROR: Command '${last_command}' failed (exit $exit_code) at line $line_no"
    echo "$(date '+%Y-%m-%d %H:%M:%S'): ERROR ${last_command} (exit $exit_code)" >> "$LOGFILE"
    exit $exit_code
}
trap error_handler ERR

mark_done() { echo "$1" >> "$STATE_FILE"; }
is_done() { grep -qx "$1" "$STATE_FILE" 2>/dev/null; }
log() { echo "$(date '+%Y-%m-%d %H:%M:%S') | $1" | tee -a "$LOGFILE"; }

# --- INPUTS ---
read -p "Enter your GitHub username (NOT org): " OWNER
read -p "Enter repository name: " REPO
REPO="${REPO// /-}"
read -p "Enter repository description: " DESC
read -p "Should the repository be public or private? (public/private): " VISIBILITY
read -p "Enter collaborator username (optional): " COLLAB

log "🚀 Starting repository automation for '$OWNER/$REPO'..."


if ! is_done "repo_created"; then
  if gh repo view "$OWNER/$REPO" >/dev/null 2>&1; then
    log "ℹ️ Repo already exists on GitHub"
  else
    gh repo create "$OWNER/$REPO" --$VISIBILITY --description "$DESC" --confirm
    log "✅ Repo created under personal account"
  fi
  mark_done "repo_created"
fi

if [ ! -d .git ]; then
  git init
  echo "# $REPO" > README.md
  git add .
  git commit -m "Initial commit"
  log "🌀 Local Git repo initialized"
fi


if ! git remote | grep -q origin; then
  git remote add origin "https://github.com/$OWNER/$REPO.git"
  log "🔗 remote origin added"
fi

if ! is_done "main_pushed"; then
  git branch -M main
  git push -u origin main
  mark_done "main_pushed"
  log "⬆️ main pushed"
fi

if ! is_done "developer_branch_created"; then
  git switch developer 2>/dev/null || git checkout -b developer
  git push -u origin developer
  git switch main
  mark_done "developer_branch_created"
  log "🌿 developer branch created + pushed"
fi


log "✅ done"