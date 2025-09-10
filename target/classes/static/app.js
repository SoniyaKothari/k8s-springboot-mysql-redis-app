// app.js
// Single place for frontend state + rendering. Exposes sync functions that dashboard.html will call.
// This file does NOT remove or change any HTML/CSS — only adds rendering/sync helpers.

(function (window, document) {
  // State (single source of truth on frontend)
  const state = {
    habits: [],           // array of { id, title, ... }
    completedHabits: [],  // array of { id, title, ... }
    expenses: []          // array of { id?, name, amount }
  };

  // Helper: safe DOM getter
  function $id(id) { return document.getElementById(id); }

  // Escape to avoid accidental HTML injection
  function escapeHtml(str) {
    if (str == null) return '';
    return String(str)
      .replace(/&/g, '&amp;')
      .replace(/</g, '&lt;')
      .replace(/>/g, '&gt;')
      .replace(/"/g, '&quot;')
      .replace(/'/g, '&#039;');
  }

  // Helper: format amount as ₹
  function formatRupee(amount) {
    return `₹${Number(amount || 0).toFixed(2)}`;
  }

  // --- Rendering functions (use state) ---
  function renderHabits() {
    const list = $id('habitList');
    if (!list) return;
    list.innerHTML = '';

    // If there are no habits show a small hint
    if (!state.habits.length) {
      const p = document.createElement('p');
      p.style.opacity = '0.7';
      p.textContent = 'No habits yet — add one!';
      list.appendChild(p);
      return;
    }

    state.habits.forEach(h => {
      const p = document.createElement('p');
      p.className = 'habit-undone';
      // content: title + Done btn
      const titleSpan = document.createElement('span');
      titleSpan.textContent = h.title;

      const btn = document.createElement('button');
      btn.textContent = 'Done';
      btn.setAttribute('data-id', String(h.id));
      btn.style.marginLeft = '10px';
      btn.addEventListener('click', () => {
        // Prefer calling global markDone (exists in dashboard.html inline script).
        // If not present, fall back to internal local mark done.
        if (typeof window.markDone === 'function') {
          window.markDone(h.id);
        } else {
          markHabitDoneLocal(h.id);
        }
      });

      // Compose
      p.appendChild(titleSpan);
      p.appendChild(btn);
      list.appendChild(p);
    });
  }

  function renderExpenses() {
    const list = $id('expenseList');
    if (!list) return;
    list.innerHTML = '';

    if (!state.expenses.length) {
      const p = document.createElement('p');
      p.style.opacity = '0.7';
      p.textContent = 'No expenses yet — add one!';
      list.appendChild(p);
      return;
    }

    state.expenses.forEach(exp => {
      const p = document.createElement('p');
      // format amount as ₹ 2 decimals
      const amt = Number(exp.amount) || 0;
      p.textContent = `${exp.name} - ${formatRupee(amt)}`;
      list.appendChild(p);
    });
  }

  function updateSummary() {
    const container = $id('summary');
    if (!container) return;

    const totalExpenses = state.expenses.reduce((s, e) => s + (Number(e.amount) || 0), 0);
    const totalHabits = (state.habits.length + state.completedHabits.length);
    const completed = state.completedHabits.length;

    container.innerHTML = `
      <h2>Weekly Summary 🌈</h2>
      <p>Total Habits: ${totalHabits}</p>
      <p>Completed Habits: ${completed}</p>
      <p>Total Expense: ${formatRupee(totalExpenses)}</p>
    `;
  }

  // --- Local-only helpers (update frontend state immediately) ---
  function markHabitDoneLocal(id) {
    const idx = state.habits.findIndex(h => Number(h.id) === Number(id));
    if (idx === -1) return;
    const done = state.habits.splice(idx, 1)[0];
    state.completedHabits.push(done);
    renderHabits();
    updateSummary();
  }

  function addHabitLocal(habitObj) {
    // habitObj should be { id, title }
    if (!habitObj || !habitObj.title) return;
    state.habits.push(habitObj);
    renderHabits();
    updateSummary();
  }

  function addExpenseLocal(expObj) {
    if (!expObj || !expObj.name) return;
    state.expenses.push(expObj);
    renderExpenses();
    updateSummary();
  }

  // --- Sync functions called by dashboard.html after backend fetches ---
  function syncHabitsFromBackend(arr) {
    if (!Array.isArray(arr)) arr = [];
    // ensure each habit has id and title
    state.habits = arr.map(h => ({ id: h.id ?? h._id ?? h.id, title: String(h.title ?? '') }));
    // NOTE: do not clear completedHabits here — backend is source-of-truth for current habits
    renderHabits();
    updateSummary();
  }

  function syncExpensesFromBackend(arr) {
    if (!Array.isArray(arr)) arr = [];
    state.expenses = arr.map(e => ({ id: e.id ?? e._id ?? null, name: String(e.name ?? ''), amount: Number(e.amount || 0) }));
    renderExpenses();
    updateSummary();
  }

  function syncCompletedFromBackend(arr) {
    if (!Array.isArray(arr)) arr = [];
    state.completedHabits = arr.map(h => ({ id: h.id ?? h._id ?? h.id, title: String(h.title ?? '') }));
    updateSummary();
  }

  // Export small API to window so dashboard.html can call them
  window.PP = window.PP || {};
  window.PP._state = state;
  window.PP.renderHabits = renderHabits;
  window.PP.renderExpenses = renderExpenses;
  window.PP.updateSummary = updateSummary;
  window.PP.syncHabitsFromBackend = syncHabitsFromBackend;
  window.PP.syncExpensesFromBackend = syncExpensesFromBackend;
  window.PP.syncCompletedFromBackend = syncCompletedFromBackend;
  window.PP.markHabitDoneLocal = markHabitDoneLocal;
  window.PP.addHabitLocal = addHabitLocal;
  window.PP.addExpenseLocal = addExpenseLocal;

  // Initial render in case HTML loads before we sync with backend
  document.addEventListener('DOMContentLoaded', function () {
    renderHabits();
    renderExpenses();
    updateSummary();
  });

})(window, document);
