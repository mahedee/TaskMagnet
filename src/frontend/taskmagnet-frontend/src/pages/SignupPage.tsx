import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import './AuthPage.css';

interface SignupForm {
  username: string;
  email: string;
  password: string;
  confirmPassword: string;
  firstName: string;
  lastName: string;
  department: string;
  jobTitle: string;
}

const SignupPage: React.FC = () => {
  const { signup } = useAuth();
  const navigate = useNavigate();

  const [formData, setFormData] = useState<SignupForm>({
    username: '',
    email: '',
    password: '',
    confirmPassword: '',
    firstName: '',
    lastName: '',
    department: '',
    jobTitle: '',
  });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');

    if (formData.password !== formData.confirmPassword) {
      setError('Passwords do not match.');
      return;
    }
    if (formData.password.length < 8) {
      setError('Password must be at least 8 characters.');
      return;
    }

    setLoading(true);
    try {
      await signup({
        username: formData.username,
        email: formData.email,
        password: formData.password,
        firstName: formData.firstName,
        lastName: formData.lastName,
        department: formData.department || undefined,
        jobTitle: formData.jobTitle || undefined,
      });
      navigate('/');
    } catch (err: unknown) {
      const msg =
        (err as { response?: { data?: { message?: string; error?: string } } })?.response?.data
          ?.message ||
        (err as { response?: { data?: { error?: string } } })?.response?.data?.error ||
        'Signup failed. Please try again.';
      setError(msg);
    } finally {
      setLoading(false);
    }
  };

  const set = (field: keyof SignupForm) => (e: React.ChangeEvent<HTMLInputElement>) =>
    setFormData((prev) => ({ ...prev, [field]: e.target.value }));

  return (
    <div className="auth-page">
      <div className="auth-container auth-container--wide">
        <div className="auth-brand">
          <span className="auth-logo">🧲</span>
          <h1>TaskMagnet</h1>
          <p>Create your account</p>
        </div>

        <form className="auth-form" onSubmit={handleSubmit}>
          {error && <div className="auth-error">{error}</div>}

          <div className="form-row">
            <div className="form-group">
              <label className="form-label">First Name *</label>
              <input type="text" className="form-control" placeholder="First name" value={formData.firstName} onChange={set('firstName')} required />
            </div>
            <div className="form-group">
              <label className="form-label">Last Name *</label>
              <input type="text" className="form-control" placeholder="Last name" value={formData.lastName} onChange={set('lastName')} required />
            </div>
          </div>

          <div className="form-group">
            <label className="form-label">Username *</label>
            <input type="text" className="form-control" placeholder="Choose a username (min 3 chars)" value={formData.username} onChange={set('username')} required minLength={3} />
          </div>

          <div className="form-group">
            <label className="form-label">Email *</label>
            <input type="email" className="form-control" placeholder="your@email.com" value={formData.email} onChange={set('email')} required />
          </div>

          <div className="form-row">
            <div className="form-group">
              <label className="form-label">Password *</label>
              <input type="password" className="form-control" placeholder="Min 8 characters" value={formData.password} onChange={set('password')} required minLength={8} />
            </div>
            <div className="form-group">
              <label className="form-label">Confirm Password *</label>
              <input type="password" className="form-control" placeholder="Repeat password" value={formData.confirmPassword} onChange={set('confirmPassword')} required />
            </div>
          </div>

          <div className="form-row">
            <div className="form-group">
              <label className="form-label">Department</label>
              <input type="text" className="form-control" placeholder="e.g. Engineering" value={formData.department} onChange={set('department')} />
            </div>
            <div className="form-group">
              <label className="form-label">Job Title</label>
              <input type="text" className="form-control" placeholder="e.g. Developer" value={formData.jobTitle} onChange={set('jobTitle')} />
            </div>
          </div>

          <button type="submit" className="btn btn-primary btn-full" disabled={loading}>
            {loading ? 'Creating account...' : 'Create Account'}
          </button>
        </form>

        <div className="auth-footer">
          Already have an account?{' '}
          <Link to="/login" className="auth-link">
            Sign in
          </Link>
        </div>
      </div>
    </div>
  );
};

export default SignupPage;
