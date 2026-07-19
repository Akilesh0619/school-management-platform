import React, { useEffect, useState } from 'react';
import api from '../api/client';
import { Calendar, CheckCircle2, XCircle, Clock, AlertCircle } from 'lucide-react';

export const AttendancePage: React.FC = () => {
  const [classes, setClasses] = useState<any[]>([]);
  const [selectedClass, setSelectedClass] = useState('');
  const [students, setStudents] = useState<any[]>([]);
  const [attendanceDate, setAttendanceDate] = useState(new Date().toISOString().split('T')[0]);
  const [attendanceRecords, setAttendanceRecords] = useState<{ [studentId: number]: string }>({});

  useEffect(() => {
    fetchClasses();
  }, []);

  const fetchClasses = async () => {
    try {
      const res = await api.get('/classes');
      setClasses(res.data);
      if (res.data.length > 0) {
        setSelectedClass(res.data[0].id);
        fetchStudents(res.data[0].id);
      }
    } catch (err) {
      console.error(err);
    }
  };

  const fetchStudents = async (classId: string) => {
    try {
      const res = await api.get(`/students/class/${classId}`);
      setStudents(res.data);
      // Initialize default PRESENT status
      const initial: { [key: number]: string } = {};
      res.data.forEach((s: any) => {
        initial[s.id] = 'PRESENT';
      });
      setAttendanceRecords(initial);
    } catch (err) {
      console.error(err);
    }
  };

  const handleStatusChange = (studentId: number, status: string) => {
    setAttendanceRecords((prev) => ({ ...prev, [studentId]: status }));
  };

  const handleSaveAttendance = async () => {
    try {
      await Promise.all(
        Object.entries(attendanceRecords).map(([studentId, status]) =>
          api.post('/attendance', {
            studentId: Number(studentId),
            date: attendanceDate,
            status,
          })
        )
      );
      alert('Attendance recorded successfully!');
    } catch (err: any) {
      alert(err.response?.data?.message || 'Attendance for today might already be marked.');
    }
  };

  return (
    <div className="space-y-6">
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold">Daily Attendance Marker</h1>
          <p className="text-sm text-slate-500">Record and track classroom attendance with automated percentage analytics.</p>
        </div>
        <button
          onClick={handleSaveAttendance}
          className="px-6 py-2.5 rounded-xl bg-gradient-to-r from-emerald-600 to-teal-600 text-white font-bold text-sm shadow-lg shadow-emerald-500/20 hover:scale-105 transition-all"
        >
          Submit Attendance Batch
        </button>
      </div>

      {/* Selector Filters */}
      <div className="glass-card p-4 flex flex-wrap items-center gap-4">
        <div>
          <label className="block text-xs font-semibold uppercase mb-1">Select Class</label>
          <select
            value={selectedClass}
            onChange={(e) => {
              setSelectedClass(e.target.value);
              fetchStudents(e.target.value);
            }}
            className="p-2.5 rounded-xl bg-slate-900/50 border border-slate-700 text-sm"
          >
            {classes.map((c) => (
              <option key={c.id} value={c.id}>{c.name}</option>
            ))}
          </select>
        </div>

        <div>
          <label className="block text-xs font-semibold uppercase mb-1">Attendance Date</label>
          <input
            type="date"
            value={attendanceDate}
            onChange={(e) => setAttendanceDate(e.target.value)}
            className="p-2.5 rounded-xl bg-slate-900/50 border border-slate-700 text-sm"
          />
        </div>
      </div>

      {/* Student Attendance Marker List */}
      <div className="glass-card overflow-hidden">
        <table className="w-full text-left border-collapse">
          <thead>
            <tr className="border-b border-slate-800 text-xs font-semibold text-slate-500 uppercase bg-slate-800/50">
              <th className="p-4">Roll</th>
              <th className="p-4">Adm No</th>
              <th className="p-4">Student Name</th>
              <th className="p-4 text-center">Status Selection</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-slate-800 text-sm">
            {students.map((student) => {
              const currentStatus = attendanceRecords[student.id] || 'PRESENT';
              return (
                <tr key={student.id} className="hover:bg-slate-800/40">
                  <td className="p-4 font-mono">{student.rollNumber}</td>
                  <td className="p-4 font-mono font-bold text-indigo-400">{student.admissionNumber}</td>
                  <td className="p-4 font-semibold">{student.name}</td>
                  <td className="p-4">
                    <div className="flex justify-center items-center space-x-2">
                      {['PRESENT', 'ABSENT', 'LATE', 'LEAVE'].map((st) => {
                        const isSelected = currentStatus === st;
                        let colorClass = 'bg-slate-800 text-slate-400';
                        if (isSelected) {
                          if (st === 'PRESENT') colorClass = 'bg-emerald-500 text-white shadow-md shadow-emerald-500/30';
                          if (st === 'ABSENT') colorClass = 'bg-rose-500 text-white shadow-md shadow-rose-500/30';
                          if (st === 'LATE') colorClass = 'bg-amber-500 text-white shadow-md shadow-amber-500/30';
                          if (st === 'LEAVE') colorClass = 'bg-indigo-500 text-white shadow-md shadow-indigo-500/30';
                        }
                        return (
                          <button
                            key={st}
                            onClick={() => handleStatusChange(student.id, st)}
                            className={`px-3 py-1.5 rounded-lg font-bold text-xs transition-all ${colorClass}`}
                          >
                            {st}
                          </button>
                        );
                      })}
                    </div>
                  </td>
                </tr>
              );
            })}
          </tbody>
        </table>
      </div>
    </div>
  );
};
