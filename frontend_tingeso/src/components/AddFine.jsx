import React from 'react';
import { useNavigate } from 'react-router-dom';
import {
    Box,
    Typography,
    Grid,
    Paper
} from '@mui/material';
import {
    Add as AddIcon,
    Inventory as InventoryIcon
} from '@mui/icons-material';

const AddFine = () => {
    const navigate = useNavigate();

    return (
        <Box sx={{ p: 3 }}>
            <Typography variant="h4" gutterBottom sx={{ color: '#0A142E', fontWeight: 'bold', mb: 4 }}>
                Agregar Multas
            </Typography>
            
            <Grid container spacing={3}>
                {/* Tarjeta para multa por daño menor */}
                <Grid size={{ xs: 12, md: 6 }}>
                    <Paper
                        sx={{
                            p: 4,
                            cursor: 'pointer',
                            transition: 'all 0.3s',
                            border: '2px solid transparent',
                            '&:hover': {
                                borderColor: '#0A142E',
                                boxShadow: '0 4px 12px rgba(10, 20, 46, 0.2)',
                                transform: 'translateY(-4px)'
                            }
                        }}
                        onClick={() => navigate('/fines/addMinorDamage')}
                    >
                        <Box sx={{ display: 'flex', alignItems: 'center', gap: 2, mb: 2 }}>
                            <Box
                                sx={{
                                    backgroundColor: '#0A142E',
                                    p: 2,
                                    borderRadius: '50%',
                                    display: 'flex',
                                    alignItems: 'center',
                                    justifyContent: 'center'
                                }}
                            >
                                <AddIcon sx={{ fontSize: 32, color: '#FACC15' }} />
                            </Box>
                            <Typography variant="h5" sx={{ color: '#0A142E', fontWeight: 'bold' }}>
                                Multa por Daño Menor
                            </Typography>
                        </Box>
                        
                        <Typography variant="body1" sx={{ color: '#666', mb: 2 }}>
                            Registra una multa por daño menor en el sistema.
                        </Typography>
                        
                        <Box component="ul" sx={{ pl: 2, color: '#888', fontSize: '0.9rem' }}>
                            <li>Requiere el préstamo asociado a la multa</li>
                            <li>Define Valor de la multa</li>
                        </Box>
                    </Paper>
                </Grid>

                {/* Tarjeta para multa por daño irreparable */}
                <Grid size={{ xs: 12, md: 6 }}>
                    <Paper
                        sx={{
                            p: 4,
                            cursor: 'pointer',
                            transition: 'all 0.3s',
                            border: '2px solid transparent',
                            '&:hover': {
                                borderColor: '#FACC15',
                                boxShadow: '0 4px 12px rgba(250, 204, 21, 0.3)',
                                transform: 'translateY(-4px)'
                            }
                        }}
                        onClick={() => navigate('/fines/addIrreparableDamage')}
                    >
                        <Box sx={{ display: 'flex', alignItems: 'center', gap: 2, mb: 2 }}>
                            <Box
                                sx={{
                                    backgroundColor: '#FACC15',
                                    p: 2,
                                    borderRadius: '50%',
                                    display: 'flex',
                                    alignItems: 'center',
                                    justifyContent: 'center'
                                }}
                            >
                                <InventoryIcon sx={{ fontSize: 32, color: '#0A142E' }} />
                            </Box>
                            <Typography variant="h5" sx={{ color: '#0A142E', fontWeight: 'bold' }}>
                                Multa por Daño Irreparable
                            </Typography>
                        </Box>
                        
                        <Typography variant="body1" sx={{ color: '#666', mb: 2 }}>
                            Registra una multa por daño irreparable en el sistema.
                        </Typography>
                        
                        <Box component="ul" sx={{ pl: 2, color: '#888', fontSize: '0.9rem' }}>
                            <li>Solo requiere el préstamo asociado a la multa</li>
                        </Box>
                    </Paper>
                </Grid>
            </Grid>
        </Box>
    );
};

export default AddFine;
