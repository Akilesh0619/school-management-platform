import React, { useEffect, useState } from 'react';
import api from '../api/client';
import { Plus, Search, Trash2, Edit, Mail, Phone, Building } from 'lucide-react';

export const Teachers: React.FC = () => {
  const [teachers, setTeachers] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [selectedTeacher, setSelectedTeacher] = useState<any>(null);

  const [formData, setFormData] = useState({
    name: '',
    email: '',
    phone: '',
    qualification: 'M.Sc. Mathematics',
    experience: 5,
    salary: 4000,
    department: 'Mathematics',
  });

  useEffect(() => {
    fetchTeachers();
  }, []);

  const fetchTeachers = async () => {
    try {
      const res = await api.get('/teachers');
      setTeachers(res.data);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      if (selectedTeacher) {
        await api.put(`/teachers/${selectedTeacher.id}`, formData);
      } else {
        await api.post('/teachers', formData);
      }
      setShowModal(false);
      fetchTeachers();
    } catch (err: any) {
      alert(err.response?.data?.message || 'Error saving teacher profile');
    }
  };

  const handleDelete = async (id: number) => {
    if (confirm('Soft-delete this teacher profile?')) {
      try {
        await api.delete(`/teachers/${id}`);
        fetchTeachers();
      } catch (err) {
        console.error(err);
      }
    }
  };

  return (
    <div className="space-y-6">
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold">Faculty & Teachers</h1>
          <p className="text-sm text-slate-500">Academic staff profiles, salary tiers, and qualification records.</p>
        </div>
        <button
          onClick={() => {
            setSelectedTeacher(null);
            setFormData({ name: '', email: '', phone: '', qualification: 'M.Sc. Mathematics', experience: 5, salary: 4000, department: 'Mathematics' });
            setShowModal(true);
          }}
          className="px-4 py-2.5 rounded-xl bg-indigo-600 hover:bg-indigo-700 text-white font-semibold text-sm flex items-center space-x-2 shadow-lg shadow-indigo-500/25 transition-all"
        >
          <Plus size={18} />
          <span>Add Faculty Member</span>
        </button>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        {teachers.map((teacher) => (
          <div key={teacher.id} className="glass-card-hover p-6 space-y-4">
            <div className="flex items-center space-x-4">
              <div className="w-14 h-14 rounded-2xl bg-gradient-to-tr from-violet-600 to-indigo-600 flex items-center justify-center text-white font-bold text-xl shadow-lg">
                {teacher.name.charAt(0)}
              </div>
              <div>
                <h3 className="font-bold text-base">{teacher.name}</h3>
                <span className="text-xs px-2 py-0.5 rounded-md bg-indigo-500/10 text-indigo-400 font-semibold uppercase">
                  {teacher.department}
                </span>
              </div>
            </div>

            <div className="space-y-2 text-xs text-slate-400 border-t border-slate-800 pt-3">
              <div className="flex items-center space-x-2">
                <Mail size={14} className="text-slate-500" />
                <span>{teacher.email}</span>
              </div>
              <div className="flex items-center space-x-2">
                <Phone size={14} className="text-slate-500" />
                <span>{teacher.phone}</span>
              </div>
              <div className="flex items-center space-x-2">
                <Building size={14} className="text-slate-500" />
                <span>{teacher.qualification} ({teacher.experience} yrs exp)</span>
              </div>
            </div>

            <div className="flex justify-between items-center border-t border-slate-800 pt-3 text-xs">
              <span className="font-bold text-emerald-400">${teacher.salary} / mo</span>
              <div className="space-x-1">
                <button
                  onClick={() => {
                    setSelectedTeacher(teacher);
                    setFormData({
                      name: teacher.name,
                      email: teacher.email,
                      phone: teacher.phone,
                      qualification: teacher.qualification,
                      experience: teacher.experience,
                      salary: teacher.salary,
                      department: teacher.department,
                    });
                    setShowModal(true);
                  }}
                  className="p-1.5 rounded-lg hover:bg-slate-800 text-amber-400"
                >
                  <Edit size={16} />
                </button>
                <button
                  onClick={() => handleDelete(teacher.id)}
                  className="p-1.5 rounded-lg hover:bg-slate-800 text-rose-400"
                >
                  <Trash2 size={16} />
                </button>
              </div>
            </div>
          </div>
        ))}
      </div>

      {/* Modal */}
      {showModal && (
        <div className="fixed inset-0 z-50 bg-black/60 backdrop-blur-sm flex items-center justify-center p-4">
          <div className="glass-card w-full max-w-lg p-6 space-y-4">
            <h2 className="text-lg font-bold">{selectedTeacher ? 'Edit Teacher' : 'Add New Teacher'}</h2>
            <form onSubmit={handleSubmit} className="space-y-3">
              <div>
                <label className="block text-xs font-semibold uppercase mb-1">Full Name</label>
                <input
                  type="text"
                  required
                  value={formData.name}
                  onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                  className="w-full p-2.5 rounded-xl bg-slate-900/50 border border-slate-700 text-sm"
                />
              </div>
              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-xs font-semibold uppercase mb-1">Email</label>
                  <input
                    type="email"
                    required
                    value={formData.email}
                    onChange={(e) => setFormData({ ...formData, email: e.target.value })}
                    className="w-full p-2.5 rounded-xl bg-slate-900/50 border border-slate-700 text-sm"
                  />
                </div>
                <div>
                  <label className="block text-xs font-semibold uppercase mb-1">Phone</label>
                  <input
                    type="text"
                    required
                    value={formData.phone}
                    onChange={(e) => setFormData({ ...formData, phone: e.target.value })}
                    className="w-full p-2.5 rounded-xl bg-slate-900/50 border border-slate-700 text-sm"
                  />
                </div>
              </div>
              <div className="flex justify-end space-x-3 pt-4">
                <button type="button" onClick={() => setShowModal(false)} className="px-4 py-2 rounded-xl bg-slate-800 text-sm font-semibold">
                  Cancel
                </button>
                <button type="submit" className="px-4 py-2 rounded-xl bg-indigo-600 text-sm font-semibold text-white">
                  Save Faculty
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
