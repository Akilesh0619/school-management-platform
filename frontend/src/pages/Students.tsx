import React, { useEffect, useState } from 'react';
import api from '../api/client';
import { Plus, Search, Trash2, Edit, QrCode } from 'lucide-react';

export const Students: React.FC = () => {
  const [students, setStudents] = useState<any[]>([]);
  const [classes, setClasses] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');
  const [showModal, setShowModal] = useState(false);
  const [selectedStudent, setSelectedStudent] = useState<any>(null);
  const [qrModalUrl, setQrModalUrl] = useState<string | null>(null);

  const [formData, setFormData] = useState({
    name: '',
    dob: '2012-01-01',
    gender: 'MALE',
    classId: '',
    phone: '',
    email: '',
    academicYear: '2026-2027',
  });

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    try {
      const [resStudents, resClasses] = await Promise.all([
        api.get('/students'),
        api.get('/classes'),
      ]);
      setStudents(resStudents.data);
      setClasses(resClasses.data);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      if (selectedStudent) {
        await api.put(`/students/${selectedStudent.id}`, formData);
      } else {
        await api.post('/students', formData);
      }
      setShowModal(false);
      fetchData();
    } catch (err: any) {
      alert(err.response?.data?.message || 'Error saving student');
    }
  };

  const handleDelete = async (id: number) => {
    if (confirm('Are you sure you want to soft-delete this student?')) {
      try {
        await api.delete(`/students/${id}`);
        fetchData();
      } catch (err) {
        console.error(err);
      }
    }
  };

  const filteredStudents = students.filter(
    (s) =>
      s.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
      s.admissionNumber.toLowerCase().includes(searchTerm.toLowerCase())
  );

  return (
    <div className="space-y-6">
      {/* Top Controls */}
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold">Student Profiles Directory</h1>
          <p className="text-sm text-slate-500">Manage enrolled student records, classes, and credentials.</p>
        </div>
        <button
          onClick={() => {
            setSelectedStudent(null);
            setFormData({ name: '', dob: '2012-01-01', gender: 'MALE', classId: classes[0]?.id || '', phone: '', email: '', academicYear: '2026-2027' });
            setShowModal(true);
          }}
          className="px-4 py-2.5 rounded-xl bg-indigo-600 hover:bg-indigo-700 text-white font-semibold text-sm flex items-center space-x-2 shadow-lg shadow-indigo-500/25 transition-all"
        >
          <Plus size={18} />
          <span>Register Student</span>
        </button>
      </div>

      {/* Search Bar */}
      <div className="relative max-w-md">
        <Search className="absolute left-3.5 top-1/2 -translate-y-1/2 text-slate-400" size={18} />
        <input
          type="text"
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          placeholder="Filter by student name or admission number..."
          className="w-full pl-11 pr-4 py-2.5 rounded-xl glass-card text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500/50"
        />
      </div>

      {/* Table */}
      <div className="glass-card overflow-hidden">
        <table className="w-full text-left border-collapse">
          <thead>
            <tr className="border-b border-slate-200/50 dark:border-slate-800/50 text-xs font-semibold text-slate-500 uppercase tracking-wider bg-slate-100/50 dark:bg-slate-800/50">
              <th className="p-4">Adm No</th>
              <th className="p-4">Name</th>
              <th className="p-4">Class</th>
              <th className="p-4">Gender</th>
              <th className="p-4">Phone</th>
              <th className="p-4">Status</th>
              <th className="p-4 text-right">Actions</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-slate-200/50 dark:divide-slate-800/50 text-sm">
            {filteredStudents.map((student) => (
              <tr key={student.id} className="hover:bg-slate-100/40 dark:hover:bg-slate-800/40 transition-colors">
                <td className="p-4 font-mono font-bold text-indigo-400">{student.admissionNumber}</td>
                <td className="p-4 font-semibold">{student.name}</td>
                <td className="p-4">{student.className}</td>
                <td className="p-4 uppercase text-xs font-semibold">{student.gender}</td>
                <td className="p-4">{student.phone || 'N/A'}</td>
                <td className="p-4">
                  <span className="px-2.5 py-1 rounded-full text-xs font-bold bg-emerald-500/10 text-emerald-500">
                    {student.status}
                  </span>
                </td>
                <td className="p-4 text-right space-x-2">
                  <button
                    onClick={() => setQrModalUrl(`/api/qr/generate/qr?text=${student.admissionNumber}`)}
                    className="p-1.5 rounded-lg hover:bg-slate-200 dark:hover:bg-slate-700 text-slate-400 hover:text-indigo-400"
                    title="Student ID QR"
                  >
                    <QrCode size={16} />
                  </button>
                  <button
                    onClick={() => {
                      setSelectedStudent(student);
                      setFormData({
                        name: student.name,
                        dob: student.dob,
                        gender: student.gender,
                        classId: student.classId,
                        phone: student.phone || '',
                        email: student.email || '',
                        academicYear: student.academicYear,
                      });
                      setShowModal(true);
                    }}
                    className="p-1.5 rounded-lg hover:bg-slate-200 dark:hover:bg-slate-700 text-slate-400 hover:text-amber-400"
                  >
                    <Edit size={16} />
                  </button>
                  <button
                    onClick={() => handleDelete(student.id)}
                    className="p-1.5 rounded-lg hover:bg-slate-200 dark:hover:bg-slate-700 text-slate-400 hover:text-rose-400"
                  >
                    <Trash2 size={16} />
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {/* Register/Edit Modal */}
      {showModal && (
        <div className="fixed inset-0 z-50 bg-black/60 backdrop-blur-sm flex items-center justify-center p-4">
          <div className="glass-card w-full max-w-lg p-6 space-y-4">
            <h2 className="text-lg font-bold">{selectedStudent ? 'Edit Student' : 'Register New Student'}</h2>
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
                  <label className="block text-xs font-semibold uppercase mb-1">Date of Birth</label>
                  <input
                    type="date"
                    required
                    value={formData.dob}
                    onChange={(e) => setFormData({ ...formData, dob: e.target.value })}
                    className="w-full p-2.5 rounded-xl bg-slate-900/50 border border-slate-700 text-sm"
                  />
                </div>
                <div>
                  <label className="block text-xs font-semibold uppercase mb-1">Class</label>
                  <select
                    value={formData.classId}
                    onChange={(e) => setFormData({ ...formData, classId: e.target.value })}
                    className="w-full p-2.5 rounded-xl bg-slate-900/50 border border-slate-700 text-sm"
                  >
                    {classes.map((c) => (
                      <option key={c.id} value={c.id}>{c.name}</option>
                    ))}
                  </select>
                </div>
              </div>
              <div className="flex justify-end space-x-3 pt-4">
                <button
                  type="button"
                  onClick={() => setShowModal(false)}
                  className="px-4 py-2 rounded-xl bg-slate-800 text-sm font-semibold"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="px-4 py-2 rounded-xl bg-indigo-600 text-sm font-semibold text-white"
                >
                  Save Profile
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* QR Preview Modal */}
      {qrModalUrl && (
        <div className="fixed inset-0 z-50 bg-black/60 backdrop-blur-sm flex items-center justify-center p-4">
          <div className="glass-card p-6 text-center space-y-4 max-w-sm">
            <h3 className="font-bold text-lg">Student ID Code</h3>
            <img src={qrModalUrl} alt="QR Code" className="mx-auto w-48 h-48 rounded-xl bg-white p-2 shadow-lg" />
            <button
              onClick={() => setQrModalUrl(null)}
              className="px-4 py-2 bg-indigo-600 text-white rounded-xl text-sm font-semibold"
            >
              Close
            </button>
          </div>
        </div>
      )}
    </div>
  );
};
