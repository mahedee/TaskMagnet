import React, { useState, useEffect } from 'react';
import { categoryService } from '../services/categoryService';
import { CategoryResponse, CategoryRequest } from '../types';
import './CategoryManager.css';

const EMPTY_FORM: CategoryRequest = { name: '', description: '' };

const CategoryManager: React.FC = () => {
  const [categories, setCategories] = useState<CategoryResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [showForm, setShowForm] = useState(false);
  const [editingCategory, setEditingCategory] = useState<CategoryResponse | null>(null);
  const [formData, setFormData] = useState<CategoryRequest>(EMPTY_FORM);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState('');
  const [searchTerm, setSearchTerm] = useState('');

  useEffect(() => {
    loadCategories();
  }, []);

  const loadCategories = async () => {
    try {
      setLoading(true);
      setCategories(await categoryService.getAll());
    } catch (err) {
      console.error('Error loading categories:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setSaving(true);
    try {
      if (editingCategory) {
        await categoryService.update(editingCategory.id, formData);
      } else {
        await categoryService.create(formData);
      }
      await loadCategories();
      resetForm();
    } catch (err: unknown) {
      const msg =
        (err as { response?: { data?: { message?: string } } })?.response?.data?.message ||
        'Failed to save category.';
      setError(msg);
    } finally {
      setSaving(false);
    }
  };

  const handleEdit = (cat: CategoryResponse) => {
    setEditingCategory(cat);
    setFormData({ name: cat.name, description: cat.description ?? '' });
    setShowForm(true);
  };

  const handleDelete = async (id: number) => {
    if (!window.confirm('Delete this category? Any linked projects/tasks will lose this category.')) return;
    try {
      await categoryService.delete(id);
      await loadCategories();
    } catch (err) {
      console.error('Error deleting category:', err);
    }
  };

  const resetForm = () => {
    setFormData(EMPTY_FORM);
    setEditingCategory(null);
    setShowForm(false);
    setError('');
  };

  const filtered = categories.filter(c =>
    c.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
    (c.description ?? '').toLowerCase().includes(searchTerm.toLowerCase())
  );

  if (loading) {
    return (
      <div className="category-manager-loading">
        <div className="spinner"></div>
        <p>Loading categories…</p>
      </div>
    );
  }

  return (
    <div className="category-manager">
      <header className="category-manager-header">
        <div>
          <h1>Categories</h1>
          <p className="category-subtitle">Organise your projects and tasks with categories</p>
        </div>
        <button className="btn btn-primary" onClick={() => setShowForm(true)}>
          + New Category
        </button>
      </header>

      {/* Search */}
      <div className="category-toolbar">
        <input
          type="text"
          className="form-control search-input"
          placeholder="Search categories…"
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
        />
        <span className="category-count">{filtered.length} categor{filtered.length === 1 ? 'y' : 'ies'}</span>
      </div>

      {/* Modal */}
      {showForm && (
        <div className="modal-overlay">
          <div className="modal modal--sm">
            <div className="modal-header">
              <h2>{editingCategory ? 'Edit Category' : 'New Category'}</h2>
              <button className="close-btn" onClick={resetForm}>×</button>
            </div>
            <form onSubmit={handleSubmit} className="category-form">
              {error && <div className="form-error">{error}</div>}
              <div className="form-group">
                <label className="form-label">Name *</label>
                <input
                  type="text"
                  className="form-control"
                  placeholder="e.g. Engineering"
                  value={formData.name}
                  onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                  required
                  autoFocus
                />
              </div>
              <div className="form-group">
                <label className="form-label">Description</label>
                <textarea
                  className="form-control"
                  rows={3}
                  placeholder="Optional description…"
                  value={formData.description ?? ''}
                  onChange={(e) => setFormData({ ...formData, description: e.target.value })}
                />
              </div>
              <div className="form-actions">
                <button type="button" className="btn btn-secondary" onClick={resetForm}>
                  Cancel
                </button>
                <button type="submit" className="btn btn-primary" disabled={saving}>
                  {saving ? 'Saving…' : editingCategory ? 'Update' : 'Create'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Grid */}
      {filtered.length === 0 ? (
        <div className="category-empty">
          {searchTerm ? (
            <>
              <span className="empty-icon">🔍</span>
              <h3>No categories match "{searchTerm}"</h3>
              <p>Try a different search term.</p>
            </>
          ) : (
            <>
              <span className="empty-icon">🏷️</span>
              <h3>No categories yet</h3>
              <p>Create your first category to organise projects and tasks.</p>
              <button className="btn btn-primary" onClick={() => setShowForm(true)}>
                + New Category
              </button>
            </>
          )}
        </div>
      ) : (
        <div className="category-list">
          <div className="category-list-header">
            <div className="category-list-col category-list-col--icon"></div>
            <div className="category-list-col category-list-col--name">Name</div>
            <div className="category-list-col category-list-col--description">Description</div>
            <div className="category-list-col category-list-col--date">Created</div>
            <div className="category-list-col category-list-col--actions">Actions</div>
          </div>
          {filtered.map(cat => (
            <div key={cat.id} className="category-list-item">
              <div className="category-list-col category-list-col--icon">
                <span className="category-icon">🏷️</span>
              </div>
              <div className="category-list-col category-list-col--name">
                <h3 className="category-name">{cat.name}</h3>
              </div>
              <div className="category-list-col category-list-col--description">
                <p className="category-description">
                  {cat.description || <span className="text-muted">No description</span>}
                </p>
              </div>
              <div className="category-list-col category-list-col--date">
                <span className="category-date">
                  {new Date(cat.createdAt).toLocaleDateString()}
                </span>
              </div>
              <div className="category-list-col category-list-col--actions">
                <button className="btn-icon" onClick={() => handleEdit(cat)} title="Edit">✏️</button>
                <button className="btn-icon btn-icon--danger" onClick={() => handleDelete(cat.id)} title="Delete">🗑️</button>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default CategoryManager;
